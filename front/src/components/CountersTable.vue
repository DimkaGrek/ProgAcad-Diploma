<script>
import {
  defineComponent,
  ref,
  computed,
  onMounted,
  onUnmounted,
  inject,
} from "vue";

export default defineComponent({
  props: ["counters", "show"],
  emits: [
    "select-counter",
    "selectUpdateCounter",
    "selectDeleteCounter",
    "registerDeleteCounters",
  ],
  setup(props, { emit }) {
    const selectedIds = ref([]);
    const selectedColumn = ref("");
    const selectedCounterId = ref(null);
    const showAttentionMessage = ref(false); // показать окно предупреждения

    const searchQuery = inject("searchQuery");
    const clearSearch = inject("clearSearch");

    const menuTop = ref(0);
    const menuLeft = ref(0);

    const selectCounter = (counterId, number, pavilion, place) => {
      const counterData = {
        couterId: counterId,
        number: number,
        pavilion: pavilion,
        place: place,
      };
      emit("select-counter", counterId, counterData);
    };

    function toggleSelectionAll(event) {
      if (event.target.checked) {
        selectedIds.value = [];
        for (let c of props.counters) {
          selectedIds.value.push(c.id);
        }
      } else {
        selectedIds.value = [];
      }
    }

    const sortedCounters = computed(() => {
      console.log("Props show: ", props.show);
      return [...props.counters].sort((counter1, counter2) =>
        String(counter1[selectedColumn.value])?.localeCompare(
          String(counter2[selectedColumn.value])
        )
      );
    });

    const sortedAndSearchCounters = computed(() => {
      return sortedCounters.value.filter(
        (counter) =>
          counter.pavilion
            .toLowerCase()
            .includes(searchQuery.value.toLowerCase()) ||
          counter.place
            .toLowerCase()
            .includes(searchQuery.value.toLowerCase()) ||
          String(counter.number).includes(searchQuery.value) ||
          counter.surname
            .toLowerCase()
            .includes(searchQuery.value.toLowerCase()) ||
          counter.name.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    });

    const showPopUpMenu = (event, counterId) => {
      console.log("вызвана showPopUpMenu ...");
      selectedCounterId.value = counterId;
      const element = event.target;
      const rect = element.getBoundingClientRect();
      console.log("rect.top: ", rect.top);
      menuTop.value = rect.top + window.pageYOffset;
      menuLeft.value = rect.left + window.pageXOffset + rect.width * 0.75;
    };

    const onMouseMove = (event) => {
      if (
        selectedCounterId.value &&
        !event.target.closest(".menu-trigger") &&
        !event.target.closest(".showmenu")
      ) {
        selectedCounterId.value = null;
      }
    };

    const removeMouseMoveListener = () => {
      window.removeEventListener("mousemove", onMouseMove);
    };

    onMounted(() => {
      window.addEventListener("mousemove", onMouseMove);
      emit("registerDeleteCounters", callDeleteCounters);
    });

    onUnmounted(() => {
      removeMouseMoveListener();
    });

    const callDeleteCounters = () => {
      console.log("Функция удаления счетчиков вызвана");
      if (selectedIds.value.length > 0) {
        showAttentionMessage.value = true;
      }
    };

    function deleteCounters() {
      showAttentionMessage.value = false;
      emit("selectDeleteCounter", selectedIds.value);
    }

    return {
      selectCounter,
      toggleSelectionAll,
      selectedIds,
      selectedColumn,
      sortedCounters,
      sortedAndSearchCounters,
      showPopUpMenu,
      selectedCounterId,
      menuTop,
      menuLeft,
      callDeleteCounters,
      showAttentionMessage,
      deleteCounters,
      searchQuery,
      clearSearch,
      props,
    };
  },
});
</script>

<template>
  <!-- <div>{{ selectedIds }}</div>
  selectedCounterId: {{ selectedCounterId }} -->
  <div class="table-scroll">
    <div v-if="showAttentionMessage" class="attention">
      <h1 class="ff-500-18">{{ $translate.t("areYouSure") }}</h1>
      <div class="add-footer">
        <button @click="showAttentionMessage = false" class="btn-tbl ff-500-14">
          {{ $translate.t("close") }}
        </button>
        <button @click="deleteCounters" class="btn-tbl ff-500-14">
          {{ $translate.t("confirm") }}
        </button>
      </div>
    </div>
    <table>
      <thead :class="{ 'disable-sticky': show }">
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
      <tbody>
        <transition-group name="counter-list">
          <tr v-for="c in sortedAndSearchCounters" :key="c.id">
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
            <td
              class="menu-trigger"
              @mouseenter="($event) => showPopUpMenu($event, c.id)"
              @mouseleave="
                () => {
                  if (selectedCounterId.value) selectedCounterId.value = null;
                }
              "
            >
              <span
                class="link-text indi"
                @click="
                  clearSearch();
                  selectCounter(c.id, c.number, c.pavilion, c.place);
                "
                >{{ c.number }}</span
              >
            </td>
            <td>
              {{ c.surname }}
            </td>
            <td>
              {{ c.name }}
            </td>
            <td>{{ c.notes }}</td>
            <td>---</td>
            <div
              v-if="selectedCounterId === c.id"
              class="showmenu"
              :style="{
                position: 'absolute',
                top: `${menuTop}px`,
                left: `${menuLeft}px`,
              }"
              @mouseenter="() => (selectedCounterId = c.id)"
              @mouseleave="() => (selectedCounterId = null)"
            >
              <div
                class="menu-text indi ff-500-14"
                @click="
                  clearSearch();
                  selectCounter(c.id, c.number, c.pavilion, c.place);
                "
              >
                {{ $translate.t("accruals") }}
              </div>
              <div
                @click="
                  (selectedCounterId = null), $emit('selectUpdateCounter', c.id)
                "
                class="menu-text brown ff-500-14"
              >
                {{ $translate.t("edit") }}
              </div>
              <div
                @click="
                  (selectedCounterId = null), $emit('selectDeleteCounter', c.id)
                "
                class="menu-text red ff-500-14"
              >
                {{ $translate.t("delete") }}
              </div>
            </div>
          </tr>
        </transition-group>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.showmenu {
  background: #ffffff;
  /* shadow-m */
  box-shadow: 0px 0px 0px 1px rgba(152, 161, 179, 0.1),
    0px 15px 35px -5px rgba(17, 24, 38, 0.15), 0px 5px 15px rgba(0, 0, 0, 0.08);
  border-radius: 6px;

  padding: 8px 6px;
  z-index: 1;
}
.menu-text {
  padding: 4px 10px;
  cursor: pointer;
}
.menu-text:hover {
  text-decoration-line: underline;
}
.attention {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  position: absolute;
  z-index: 1;
  margin: auto;
  background: #f7f8fa;
  padding: 10px;
  border: 1px solid #aa5b00;
  border-radius: 8px;
  text-align: center;
}
</style>
