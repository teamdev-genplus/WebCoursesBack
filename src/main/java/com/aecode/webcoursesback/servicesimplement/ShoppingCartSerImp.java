package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.CourseCartDTO;
import com.aecode.webcoursesback.dtos.ModuleCartDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.ShoppingCart;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.ICourseRepo;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IShoppingCartRepo;
import com.aecode.webcoursesback.services.IShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartSerImp implements IShoppingCartService{
    @Autowired
    private IShoppingCartRepo scR;

    @Autowired
    private IModuleRepo mR;
    @Autowired
    private ICourseRepo cR;

    @Override
    public List<CourseCartDTO> getCartByUser(Long userId) {
        List<ShoppingCart> cartItems = scR.findByUserProfile_UserId(userId);

        Map<Long, List<ShoppingCart>> itemsByCourse = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getModule().getCourse().getCourseId()));

        List<CourseCartDTO> coursesInCart = new ArrayList<>();

        for (Map.Entry<Long, List<ShoppingCart>> entry : itemsByCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<ShoppingCart> items = entry.getValue();

            Course course = cR.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            List<Module> allModules = course.getModules();

            List<ModuleCartDTO> moduleDTOs = allModules.stream()
                    .map(module -> {
                        boolean selected = items.stream()
                                .filter(ci -> ci.getModule().getModuleId().equals(module.getModuleId()))
                                .anyMatch(ci -> ci.isSelected());
                        return ModuleCartDTO.builder()
                                .moduleId(module.getModuleId())
                                .programTitle(module.getProgramTitle())
                                .priceRegular(module.getPriceRegular())
                                .promptPaymentPrice(module.getPromptPaymentPrice())
                                .selected(selected)
                                .build();
                    })
                    .collect(Collectors.toList());

            CourseCartDTO courseDTO = CourseCartDTO.builder()
                    .courseId(course.getCourseId())
                    .principalImage(course.getPrincipalImage())
                    .title(course.getTitle())
                    .cantTotalHours(course.getCantTotalHours())
                    .modules(moduleDTOs)
                    .build();

            coursesInCart.add(courseDTO);
        }

        return coursesInCart;
    }

    @Override
    public void addModuleToCart(Long userId, Long moduleId) {
        Optional<ShoppingCart> existing = scR.findByUserProfile_UserIdAndModule_ModuleId(userId, moduleId);
        if (existing.isEmpty()) {
            Module module = mR.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found"));
            UserProfile user = new UserProfile();
            user.setUserId(userId);
            ShoppingCart newItem = ShoppingCart.builder()
                    .userProfile(user)
                    .module(module)
                    .selected(true)
                    .build();
            scR.save(newItem);
        }
    }

    @Override
    public void updateModuleSelection(Long userId, Long moduleId, boolean selected) {
        ShoppingCart item = scR.findByUserProfile_UserIdAndModule_ModuleId(userId, moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        item.setSelected(selected);
        scR.save(item);
    }

    @Override
    public void removeCartItemById(Long cartId) {
        scR.deleteById(cartId);
    }
    @Override
    @Transactional
    public void removeAllModulesFromCourse(Long userId, Long courseId) {
        // Obtener todos los módulos del curso
        List<Module> modules = mR.findByCourse_CourseId(courseId);

        // Obtener lista de moduleIds
        List<Long> moduleIds = modules.stream()
                .map(Module::getModuleId)
                .collect(Collectors.toList());

        // Eliminar todos los registros del carrito que coincidan con userId y moduleId en la lista
        scR.deleteByUserProfile_UserIdAndModule_ModuleIdIn(userId, moduleIds);
    }
}
