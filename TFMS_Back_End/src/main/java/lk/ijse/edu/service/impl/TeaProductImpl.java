package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.TeaProductDto;
import lk.ijse.edu.entity.TeaProduct;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.TeaProductRepository;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeaProductImpl implements TeaProductService {
    private final TeaProductRepository teaProductRepository;

    private String generateNextProductId(String lastId) {
        if (lastId == null || lastId.isEmpty()) return "P-00001";

        String numericStr = lastId.substring(2);
        int number = Integer.parseInt(numericStr);

        number++;

        if (number > 99999) {
            throw new IllegalStateException("ID range exceeded. Maximum allowed is 99999.");
        }

        return String.format("P-%05d", number);
    }

    @Transactional
    @Override
    public String saveTeaProduct(TeaProductDto teaProductDto) {
        if (teaProductRepository.existsByName(teaProductDto.getName())) {
            throw new RuntimeException("Tea Product name already exists");
        }

        if (teaProductDto == null) {
            throw new IllegalArgumentException("Tea Product DTO cannot be null");
        }

        String lastId = teaProductRepository.findLastProductId();
        String newProductId = generateNextProductId(lastId);

        TeaProduct teaProduct = TeaProduct.builder()
                .id(newProductId)
                .name(teaProductDto.getName())
                .description(teaProductDto.getDescription())
                .price(teaProductDto.getPrice())
                .quantity(teaProductDto.getQuantity())
                .build();
        teaProductRepository.save(teaProduct);
        return "Tea Product saved successfully";
    }

    @Transactional
    @Override
    public String updateTeaProduct(TeaProductDto teaProductDto) {
        if (teaProductDto == null|| teaProductDto.getId()==null) {
            throw new IllegalArgumentException("Update Tea Product DTO cannot be null");
        }
        TeaProduct existingTeaProducts = teaProductRepository.findById(teaProductDto.getId())
                .orElseThrow(() -> new RuntimeException("Tea Product not found"));

        existingTeaProducts.setName(teaProductDto.getName());
        existingTeaProducts.setDescription(teaProductDto.getDescription());
        existingTeaProducts.setPrice(teaProductDto.getPrice());
        existingTeaProducts.setQuantity(teaProductDto.getQuantity());
        teaProductRepository.save(existingTeaProducts);
        return "Tea Product updated successfully";
    }

    @Transactional
    @Override
    public void deleteTeaProduct(String id) {
        TeaProduct teaProduct = teaProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Tea Product not found"));
        teaProductRepository.delete(teaProduct);
    }

    @Override
    public List<TeaProductDto> getAllTeaProducts() {
        List<TeaProduct> allTeaProducts = teaProductRepository.findAll();
        return allTeaProducts.stream().map(teaProduct -> {
            TeaProductDto dto = new TeaProductDto();
            dto.setId(teaProduct.getId());
            dto.setName(teaProduct.getName());
            dto.setDescription(teaProduct.getDescription());
            dto.setPrice(teaProduct.getPrice());
            dto.setQuantity(teaProduct.getQuantity());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TeaProductDto> searchTeaProduct(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }
        List<TeaProduct> allTeaProducts = teaProductRepository.findTeaProductByNameContainingIgnoreCase(keyword);

        if (allTeaProducts.isEmpty()){
            throw new ResourceNotFound("No Tea Maker Found");
        }

        return allTeaProducts.stream().map(teaProduct -> {
            TeaProductDto dto = new TeaProductDto();
            dto.setId(teaProduct.getId());
            dto.setName(teaProduct.getName());
            dto.setDescription(teaProduct.getDescription());
            dto.setPrice(teaProduct.getPrice());
            dto.setQuantity(teaProduct.getQuantity());
            return dto;
        }).collect(Collectors.toList());
    }
}
