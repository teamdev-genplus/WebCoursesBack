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
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
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
    @Autowired
    private IUserProfileRepository upR;

    @Override
    public List<CourseCartDTO> getCartByUser(String clerkId) {
        List<ShoppingCart> cartItems = scR.findByUserProfile_clerkId(clerkId);

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

            //calcular descuento de curso fullprice - pricewithdiscount
            double descuento= course.getFullprice()-course.getPricewithdiscount();

            CourseCartDTO courseDTO = CourseCartDTO.builder()
                    .courseId(course.getCourseId())
                    .principalImage(course.getPrincipalImage())
                    .title(course.getTitle())
                    .cantTotalHours(course.getCantTotalHours())
                    .discount(descuento)
                    .discountPercentage(course.getDiscountPercentage())
                    .pricewithdiscount(course.getPricewithdiscount())
                    .modules(moduleDTOs)
                    .build();

            coursesInCart.add(courseDTO);
        }

        return coursesInCart;
    }

    @Override
    public void addModuleToCart(String clerkId, Long moduleId) {
        Optional<ShoppingCart> existing = scR.findByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);
        if (existing.isEmpty()) {
            Module module = mR.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found"));

            // ✅ Aquí usamos el repositorio para obtener un UserProfile persistido
            UserProfile user = upR.findByClerkId(clerkId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with clerkId: " + clerkId));


            ShoppingCart newItem = ShoppingCart.builder()
                    .userProfile(user)
                    .module(module)
                    .selected(true)
                    .build();

            scR.save(newItem);
        }
    }

    @Override
    public void updateModuleSelection(String clerkId, Long moduleId, boolean selected) {
        ShoppingCart item = scR.findByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId)
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
    public void removeAllModulesFromCourse(String clerkId, Long courseId) {
        // Obtener todos los módulos del curso
        List<Module> modules = mR.findByCourse_CourseId(courseId);

        // Obtener lista de moduleIds
        List<Long> moduleIds = modules.stream()
                .map(Module::getModuleId)
                .collect(Collectors.toList());

        // Eliminar todos los registros del carrito que coincidan con userId y moduleId en la lista
        scR.deleteByUserProfile_ClerkIdAndModule_ModuleIdIn(clerkId, moduleIds);
    }

    @Override
    @Transactional
    public void removeCartItemByClerkIdAndModuleId(String clerkId, Long moduleId) {
        // Validar existencia de usuario
        boolean userExists = upR.existsByClerkId(clerkId);
        if (!userExists) {
            throw new EntityNotFoundException("No existe un usuario con clerkId: " + clerkId);
        }

        // Validar existencia de módulo
        boolean moduleExists = mR.existsById(moduleId);
        if (!moduleExists) {
            throw new EntityNotFoundException("No existe un módulo con id: " + moduleId);
        }

        // Validar que el item esté en el carrito
        Optional<ShoppingCart> cartItem = scR.findByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);
        if (cartItem.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el módulo con id " + moduleId + " en el carrito del usuario " + clerkId);
        }

        // Eliminar
        scR.delete(cartItem.get());
    }


    @Override
    public void addModulesToCart(String clerkId, List<Long> moduleIds) {
        UserProfile user = upR.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with clerkId: " + clerkId));

        for (Long moduleId : moduleIds) {
            Optional<ShoppingCart> existing = scR.findByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);
            if (existing.isEmpty()) {
                Module module = mR.findById(moduleId)
                        .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + moduleId));

                ShoppingCart newItem = ShoppingCart.builder()
                        .userProfile(user)
                        .module(module)
                        .selected(true)
                        .build();

                scR.save(newItem);
            }
        }
    }
}
