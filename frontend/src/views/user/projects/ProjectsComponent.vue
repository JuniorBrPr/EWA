<template>
  <v-container fluid>
    <base-card class="mt-1" color="secondary" title="Projects">
      <v-toolbar flat>
        <v-text-field v-model="search" label="Search Project" prepend-inner-icon="$search" variant="outlined">
        </v-text-field>
        <v-spacer></v-spacer>
      </v-toolbar>
      <v-data-table
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="projects"
        :search="search"
        class="elevation-1"
        item-value="id">
        <template v-slot:top>
          <v-toolbar flat>
            <v-dialog v-model="dialogDetail" max-width="800px">
              <v-card>
                <v-tabs v-model="tab" bg-color="transparent">
                  <v-tab value="details">Project Detail</v-tab>
                  <v-tab value="tasks">Project Tasks</v-tab>
                  <v-tab value="materials">Project Materials</v-tab>
                </v-tabs>
                <v-card-text>
                  <v-window v-model="tab">
                    <v-window-item value="details">
                      <v-container>
                        <v-list>
                          <v-list-item title="Name" :subtitle="selectedProject.name"/>
                          <v-list-item title="Start Date" :subtitle="selectedProject.startDate"/>
                          <v-list-item title="End Date" :subtitle="selectedProject.endDate"/>
                          <v-list-item title="Status" :subtitle="selectedProject.status"/>
                          <v-list-item title="Team" :subtitle="teams.find(t => t.id === selectedProject.teamId)?.name"/>
                          <v-list-item title="Description" :subtitle="selectedProject.description"/>
                        </v-list>
                      </v-container>
                    </v-window-item>
                    <v-window-item value="tasks">
                      <v-container>
                        <h2>
                          In development
                        </h2>
                      </v-container>
                    </v-window-item>
                    <v-window-item value="materials">
                      <v-container>
                        <h2>
                          In development
                        </h2>
                      </v-container>
                    </v-window-item>
                  </v-window>
                </v-card-text>
              </v-card>
            </v-dialog>
          </v-toolbar>
        </template>
        <template v-slot:[`item.status`]="{ item }">
          <v-chip :color="getStatusColor(item)" :text="getStatusDisplayName(item.status)">
          </v-chip>
        </template>
        <template v-slot:[`item.teamName`]="{ item }">
          {{ this.teams.find(t => t.id === item.teamId)?.name }}
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon @click="seeDetails(item)">$info</v-icon>
        </template>
      </v-data-table>
    </base-card>
  </v-container>

</template>

<script>

import {Project} from "@/models/Project.js";
import BaseCard from "@/components/base/BaseCard.vue";
export default {
  // TODO Add Team Authenthicatio to this view
  // TODO Fix Team in List items
  name: "ProjectsComponent.vue",
  computed: {
    Project() {
      return Project
    }
  },
  components: {BaseCard},
  inject: ["projectsService", "teamsService"],
  data() {
    return {
      headers: [
        {title: "Name", key: "name"},
        {title: "Team", key: "teamName"},
        {title: 'Start Date', key: 'startDate'},
        {title: 'End Date', key: 'endDate'},
        {text:  "Status", key: "status"},
        {title: "Description", key: "description"},
        {title: "Actions", key: "actions", sortable: false}],
      projects: [],
      teams: [],
      selectedProject: new Project(),
      search: "",
      tab: "",
      itemsPerPage: 10,
      dialogDetail: false,
    }
  },

  async mounted() {
    await this.initialize();
  },

  watch: {
    dialogDetail(val) {
      val || this.close();
    },
  },

  methods: {
    async initialize() {
      await this.getProjects();
      await this.getTeams();
    },

    async getTeams() {
      this.teams = await this.teamsService.asyncGetAll();
      console.log(this.teams);
    },

    async getProjects() {
      this.projects = await this.projectsService.asyncGetAll();
      console.log(this.projects);
    },

    getStatusColor(project) {
      switch (project.status) {
        case "PENDING":
          return "yellow";
        case "CONFIRMED":
          return "green";
        case "IN_PROGRESS":
          return "blue";
        case "FINISHED":
          return "green";
        case "CANCELED":
          return "red";
        default:
          return "grey";
      }
    },

    getStatusDisplayName(status) {
      return Project.statusList.find(s => s.value === status)?.displayName;
    },

    assignSelectedProject(project) {
      this.selectedProject = Object.assign(new Project(), project);
    },

    seeDetails(project) {
      console.log(project)
      this.assignSelectedProject(project);
      console.log(this.selectedProject);
      console.log(this.editedProject);
      this.dialogDetail = true;
    },

    close() {
      this.assignSelectedProject(new Project())
      this.dialogDetail = false;
    },
  },
}
</script>

<style scoped>
</style>