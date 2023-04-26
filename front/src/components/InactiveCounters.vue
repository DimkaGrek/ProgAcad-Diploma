<script>
import { ref } from "vue";

export default {
  props: ["inactiveCounters"],
  emits: ["undeleteCounters"],
  setup(props, { emit }) {
    const selectedIds = ref([]);
    const isDropdownVisible = ref(false);
    const menuTop = ref(0);
    const menuLeft = ref(0);

    function toggleSelectionAll(event) {
      if (event.target.checked) {
        selectedIds.value = [];
        for (let c of props.inactiveCounters) {
          selectedIds.value.push(c.id);
        }
      } else {
        selectedIds.value = [];
      }
    }

    async function undeleteCounters() {
      await fetch("api/counters/undelete/" + selectedIds.value, {
        method: "POST",
      })
        .then((response) => {
          if (response.ok) {
            console.log("Счетчик(и) восстановлен(ы)!");
            selectedIds.value.length > 1
              ? alert("Лічильники успішно відновлені!")
              : alert("Лічильник успішно видновлен!");
            emit("undeleteCounters");
          } else {
            // Обработка ошибки
            response.text().then((text) => {
              console.error("Ошибка " + response.status + " - " + text);
            });
            if (response.status == 409) {
              // state.existCounter = true;
              // form.number.valid = false;
            }
          }
        })
        .catch((error) => console.error("Ошибка сети: " + error));
    }

    const showDropdown = (event) => {
      console.log("showDropdown...");
      const element = event.target;
      const rect = element.getBoundingClientRect();
      menuTop.value = rect.top + rect.height;
      menuLeft.value = rect.left;
      isDropdownVisible.value = true;
    };

    const hideDropdown = () => {
      isDropdownVisible.value = false;
    };

    return {
      props,
      selectedIds,
      toggleSelectionAll,
      isDropdownVisible,
      menuTop,
      menuLeft,
      showDropdown,
      hideDropdown,
      undeleteCounters,
    };
  },
};
</script>

<template>
  <div class="table-scroll">
    <table>
      <thead>
        <tr>
          <th class="th-checkbox" @mouseover="showDropdown">
            <label for="checkbox" class="checkbox-label">
              <input
                @change="toggleSelectionAll"
                id="checkbox"
                type="checkbox"
                class="checkbox-input"
                :checked="selectedIds.length > 0"
              />
              <span class="checkbox-custom"></span>
            </label>
          </th>
          <th @click="selectedColumn = 'pavilion'">
            {{ $translate.t("tablePavilionUpper") }}
          </th>
          <th @click="selectedColumn = 'place'">
            {{ $translate.t("tablePlaceUpper") }}
          </th>
          <th @click="selectedColumn = 'number'">
            {{ $translate.t("tableCounterUpper") }}
          </th>
          <th @click="selectedColumn = 'surname'">
            {{ $translate.t("surnameUpper") }}
          </th>
          <th @click="selectedColumn = 'name'">
            {{ $translate.t("nameUpper") }}
          </th>
          <th>{{ $translate.t("infoUpper") }}</th>
          <th>{{ $translate.t("tablePaymentUpper") }}</th>
        </tr>
      </thead>
      <div
        class="dropdown-menu"
        :style="{
          position: 'absolute',
          top: `${menuTop}px`,
          left: `${menuLeft}px`,
        }"
        v-show="isDropdownVisible && selectedIds.length > 0"
        @mouseleave="hideDropdown"
      >
        <div class="menu-text indi ff-500-14" @click="undeleteCounters">
          {{ $translate.t("restore") }}
        </div>
      </div>
      <tbody>
        <transition-group name="counter-list">
          <tr v-for="c in inactiveCounters" :key="c.id">
            <td>
              <label :for="c.id" class="checkbox-label">
                <input
                  :id="c.id"
                  type="checkbox"
                  :value="c.id"
                  v-model="selectedIds"
                  class="checkbox-input"
                />
                <span class="checkbox-custom"></span>
              </label>
            </td>
            <td>
              {{ c.pavilion }}
            </td>
            <td>
              {{ c.place }}
            </td>
            <td>
              {{ c.number }}
            </td>
            <td>
              {{ c.surname }}
            </td>
            <td>
              {{ c.name }}
            </td>
            <td>{{ c.notes }}</td>
            <td>---</td>
          </tr>
        </transition-group>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.dropdown-menu {
  /* top: 0;
  right: 40; */
  background: #ffffff;
  /* shadow-m */
  box-shadow: 0px 0px 0px 1px rgba(152, 161, 179, 0.1),
    0px 15px 35px -5px rgba(17, 24, 38, 0.15), 0px 5px 15px rgba(0, 0, 0, 0.08);
  border-radius: 6px;

  padding: 8px 6px;
  z-index: 2;
}
.menu-text {
  padding: 4px 10px;
  cursor: pointer;
}
.menu-text:hover {
  text-decoration-line: underline;
}
</style>
