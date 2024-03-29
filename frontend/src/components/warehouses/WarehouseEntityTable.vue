<template>
  <entity-data-table :dialog-config="dialogConfig" :filter-config="filterConfig" :table-config="tableConfig"
                     title="Warehouses" @delete="handleDelete" @save="handleSave"
                     @update-tableConfig="tableConfig = $event" @update-dialogConfig="dialogConfig = $event"
                     @update-filterConfig="filterConfig = $event"/>
</template>

<script>
import EntityDataTable from "@/components/base/EntityDataTable.vue";
import {Warehouse} from "@/models/Warehouse";

export default {
  name: "WarehouseEntityTable",
  inject: ['warehousesService'],
  components: {EntityDataTable},
  data() {
    return {
      warehouses: [],
      tableConfig: {
        entityName: 'Warehouse',
        headers: [
          {title: 'Name', value: 'name', key: 'name'},
          {title: 'Location', value: 'location', key: 'location'},
          {title: 'Contact Name', value: 'contactName', key: 'contactName'},
          {title: 'Actions', value: 'actions', sortable: false}
        ],
        items: this.warehouses,
        itemsPerPage: 10,
        searchTerm: '',
        actions: [
          {action: 'Details', icon: '$info', color: 'primary'},
          {action: 'Edit', icon: '$edit', color: 'primary'},
        ],
        canAdd: true,
      },
      dialogConfig: {
        open: false,
        title: '',
        item: {},
        baseObject: new Warehouse(),
        itemFields: [
          {name: 'name', label: 'Name', type: 'text', required: true},
          {name: 'address', label: 'Address', type: 'text', required: true},
          {name: 'location', label: 'Location', type: 'text', required: true},
          {name: 'postcode', label: 'Postcode', type: 'text', required: true},
          {name: 'country', label: 'Country', type: 'text', required: true},
          {name: 'contactName', label: 'Contact Name', type: 'text', required: true},
          {name: 'contactEmail', label: 'Contact Email', type: 'text', required: true},
          {name: 'contactPhone', label: 'Contact Phone', type: 'text', required: true},
        ],
        detailTabs: [
          {title: 'Stock', component: 'WarehouseStockLevelsTable'},
          {title: 'Teams', component: 'WarehouseTeamsTable'},
          {title: 'Projects', component: 'WarehouseProjectsTable'},
          {title: 'Transactions', component: 'WarehouseTransactionsTable'},
        ]
      },
      filterConfig: {
        canSearch: true,
        canSortByWarehouse: false,
        canSortByTeam: false,
        selectedWarehouse: null
      }
    }
  },
  async mounted() {
    await this.initialize();
  },
  methods: {
    async initialize() {
      this.warehouses = await this.warehousesService.asyncFindAll();
      this.tableConfig.items = this.warehouses;
      this.dialogConfig.itemFields[0].items = this.warehouses;
    },
    async handleSave(item) {
      if (item.id) {
        await this.warehousesService.asyncUpdate(item.id, item);
      } else {
        await this.warehousesService.asyncAdd(item)
      }
      await this.initialize();
    },
    async handleDelete(item) {
      const deletedItem = await this.warehousesService.asyncDeleteById(item.id);
      if (deletedItem) {
        this.dialogConfig.item = Object.assign({}, this.dialogConfig.baseObject);
        this.dialogConfig.open = false;
      } else {
        console.error("Failed to delete item");
      }
      await this.initialize();
    }
  }
}
</script>