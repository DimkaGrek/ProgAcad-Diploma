<script>
import { ref, computed } from "vue";

export default {
  props: ["arendators", "searchQuery", "show"],
  emits: ["selectUpdateArendator"],
  setup(props) {
    const selectedIds = ref([]);
    const selectedColumn = ref("");

    function toggleSelectionAll(event) {
      if (event.target.checked) {
        selectedIds.value = [];
        for (let a of props.arendators) {
          selectedIds.value.push(a.id);
        }
      } else {
        selectedIds.value = [];
      }
    }

    const sortedArendators = computed(() => {
      console.log("Props show: ", props.show);
      return [...props.arendators].sort((arend1, arend2) =>
        String(arend1[selectedColumn.value])?.localeCompare(
          String(arend2[selectedColumn.value])
        )
      );
    });

    const sortedAndSearchArendators = computed(() => {
      return sortedArendators.value.filter(
        (arendator) =>
          arendator.surname
            .toLowerCase()
            .includes(props.searchQuery.toLowerCase()) ||
          arendator.name
            .toLowerCase()
            .includes(props.searchQuery.toLowerCase()) ||
          arendator.phone1
            .toLowerCase()
            .includes(props.searchQuery.toLowerCase()) ||
          arendator.notes
            .toLowerCase()
            .includes(props.searchQuery.toLowerCase())
      );
    });

    return {
      selectedIds,
      toggleSelectionAll,
      sortedArendators,
      sortedAndSearchArendators,
    };
  },
};
</script>

<template>
  <div class="table-scroll">
    <table>
      <thead>
        <tr>
          <th class="th-checkbox">
            <label for="checkbox" class="checkbox-label">
              <input
                @change="toggleSelectionAll"
                id="checkbox"
                type="checkbox"
                class="checkbox-input"
              />
              <span class="checkbox-custom"></span>
            </label>
          </th>
          <th @click="selectedColumn = 'surname'">
            {{ $translate.t("surnameUpper") }}
          </th>
          <th @click="selectedColumn = 'name'">
            {{ $translate.t("nameUpper") }}
          </th>
          <th @click="selectedColumn = 'phone1'">
            {{ $translate.t("phone1Upper") }}
          </th>
          <th @click="selectedColumn = 'notes'">
            {{ $translate.t("infoUpper") }}
          </th>
          <th>{{ $translate.t("actionsUpper") }}</th>
        </tr>
      </thead>
      <tbody>
        <transition-group name="counter-list">
          <tr v-for="a in sortedAndSearchArendators" :key="a.id">
            <td>
              <label :for="a.id" class="checkbox-label">
                <input
                  :id="a.id"
                  type="checkbox"
                  :value="a.id"
                  v-model="selectedIds"
                  class="checkbox-input"
                />
                <span class="checkbox-custom"></span>
              </label>
            </td>
            <td>{{ a.surname }}</td>
            <td>{{ a.name }}</td>
            <td>{{ a.phone1 }}</td>
            <td>{{ a.notes }}</td>
            <td>
              <img
                @click="$emit('selectUpdateArendator', a.id)"
                src="../components/icons/edit.svg"
                alt="edit"
                class="actions"
              />
              <img
                src="../components/icons/remove.svg"
                alt="remove"
                class="actions"
              />
            </td>
          </tr>
        </transition-group>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.actions {
  width: 16px;
  margin-left: 5px;
}
.actions:hover {
  cursor: pointer;
}
</style>
