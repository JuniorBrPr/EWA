<template>
  <v-card>
    <v-data-table :headers="table.headers" :items="tableConfig.items" :itemsPerPage="table.itemsPerPage"
                  :search="table.searchTerm">
      <template v-slot:top>
        <v-row class="mt-1 p-1">
          <v-col cols="12" :md="table.canAdd ? '10' : '12'">
            <slot name="filter"/>
          </v-col>
          <v-col v-if="table.canAdd" class="mt-md-2" cols="12" md="2">
            <v-btn :block="true" color="primary" rounded="sm" variant="elevated" @click="openNewItemDialog">
              New {{ tableConfig.entityName }}
            </v-btn>
          </v-col>
        </v-row>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon v-for="(action, key) in tableConfig.actions" :key="key" @click="handleAction(action, item)">
          {{ action.icon }}
        </v-icon>
      </template>
      <template v-slot:[`item.status`]="{ item }">
        <v-chip :color="Project.getStatusColor(item)" :text="Project.getStatusDisplayName(item.status)"/>
      </template>
    </v-data-table>
    <form-dialog v-if="dialogConfig && dialog.open" :detail-tabs="dialog.detailTabs" :item="dialog.item"
                      :item-fields="dialog.itemFields" :max-width="dialog.maxWidth" :open="dialog.open"
                      :title="dialog.title" @close="handleClose" @delete="handleDelete" @save="handleSave"
    :has-generated-details="dialog.hasGeneratedDetails"/>
  </v-card>
</template>

<script>
import FormDialog from "@/components/base/FormDialog.vue";
import {Project} from "@/models/Project";

export default {
  name: "DataTable",
  computed: {
    Project() {
      return Project
    }
  },
  components: {
    FormDialog
  },
  props: {
    tableConfig: {
      type: Object,
      required: true,
    },
    dialogConfig: {
      type: Object,
      required: false,
    }
  },
  data() {
    return {
      table: {
        headers: [],
        items: [],
        itemsPerPage: 10,
        searchTerm: '',
        actions: [],
      },
      dialog: {
        open: false,
        title: '',
        item: {},
        itemFields: [],
        detailTabs: [],
        maxWidth: '800px',
      },
    };
  },
  watch: {
    dialogConfig: {
      handler: function (newVal) {
        this.dialog.title = newVal.title;
        this.dialog.item = newVal.item;
        this.dialog.itemFields = newVal.itemFields;
        this.dialog.detailTabs = newVal.detailTabs;
        this.dialog.open = newVal.open;
        this.dialog.maxWidth = newVal.maxWidth;
        this.dialog.hasGeneratedDetails = newVal.hasGeneratedDetails;
      },
      deep: true
    },
    tableConfig: {
      handler: function (newVal) {
        this.table.headers = newVal.headers;
        this.table.items = newVal.items;
        this.table.itemsPerPage = newVal.itemsPerPage;
        this.table.searchTerm = newVal.searchTerm;
        this.table.actions = newVal.actions;
        this.table.canAdd = newVal.canAdd;
      },
      deep: true
    },
  },
  created() {
    this.actions = [...this.tableConfig.actions];
  },
  methods: {
    handleAction(action, item) {
      this.$emit('action', {action: action, item: item});
    },
    openNewItemDialog() {
      this.dialog.title = "New";
      this.dialog.item = Object.assign({}, this.dialogConfig.baseObject);
      this.dialog.open = true;
    },
    handleSave(item) {
      this.$emit('save', item);
    },
    handleDelete(item) {
      this.$emit('delete', item);
    },
    handleClose() {
      this.dialog.item =
      this.dialog.open = false;
    }
  },
};
</script>