package teamx.app.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamx.app.backend.models.ProductCategory;
import teamx.app.backend.models.Warehouse;
import teamx.app.backend.models.WarehouseProductCategoryCapacity;
import teamx.app.backend.repositories.ProductCategoryRepository;
import teamx.app.backend.repositories.WarehouseProductCategoryCapacityRepository;
import teamx.app.backend.repositories.WarehouseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductCategoryCapacityRepository warehouseProductCategoryCapacityRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseProductCategoryCapacityRepository warehouseProductCategoryCapacityRepository, ProductCategoryRepository productCategoryRepository) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseProductCategoryCapacityRepository = warehouseProductCategoryCapacityRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) {
            return null;
        }
        return warehouses;
    }

    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElse(null);
    }

    public Warehouse addWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouseById(Long id, Warehouse warehouse) {
        Warehouse existingWarehouse = warehouseRepository.findById(id).orElse(null);
        if (existingWarehouse == null || warehouse == null || !Objects.equals(warehouse.getId(), id)) {
            return null;
        }
        existingWarehouse.setName(warehouse.getName());
        existingWarehouse.setLocation(warehouse.getLocation());
        existingWarehouse.setAddress(warehouse.getAddress());
        existingWarehouse.setPostcode(warehouse.getPostcode());
        existingWarehouse.setCountry(warehouse.getCountry());
        existingWarehouse.setContactName(warehouse.getContactName());
        existingWarehouse.setContactEmail(warehouse.getContactEmail());
        existingWarehouse.setContactPhone(warehouse.getContactPhone());
        return warehouseRepository.save(existingWarehouse);
    }

    public Warehouse deleteWarehouseById(Long id) {
        Warehouse existingWarehouse = warehouseRepository.findById(id).orElse(null);
        if (existingWarehouse == null) {
            return null;
        }
        warehouseRepository.deleteById(id);
        return existingWarehouse;
    }

    public List<WarehouseProductCategoryCapacity> getWarehouseCapacity(Long id) {
        List<WarehouseProductCategoryCapacity> warehouseCapacity =
                warehouseProductCategoryCapacityRepository.findAllByWarehouseId(id);
        warehouseCapacity.addAll(getMissingWarehouseCapacityCategories(id));
        return warehouseCapacity;
    }

    public List<WarehouseProductCategoryCapacity> getMissingWarehouseCapacityCategories(Long id) {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        List<WarehouseProductCategoryCapacity> warehouseCapacity = warehouseProductCategoryCapacityRepository
                .findAllByWarehouseId(id);

        for (WarehouseProductCategoryCapacity capacity : warehouseCapacity) {
            productCategories.removeIf(productCategory -> Objects.equals(productCategory.getId(),
                    capacity.getProductCategory().getId()));
        }

        List<WarehouseProductCategoryCapacity> missingWarehouseCapacity = new ArrayList<>();

        for (ProductCategory productCategory : productCategories) {
            WarehouseProductCategoryCapacity capacity = new WarehouseProductCategoryCapacity();
            capacity.setProductCategory(productCategory);
            capacity.setCapacity(0);
            capacity.setMinimumStockLevel(0);
            capacity.setWarehouse(warehouseRepository.findById(id).orElse(null));
            missingWarehouseCapacity.add(capacity);
        }

        return missingWarehouseCapacity;
    }

    public WarehouseProductCategoryCapacity addWarehouseCapacity(Long id, WarehouseProductCategoryCapacity capacity) {
        Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            return null;
        }
        capacity.setWarehouse(warehouse);
        return warehouseProductCategoryCapacityRepository.save(capacity);
    }
}
