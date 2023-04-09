<script>
import CounterForm from "../components/CounterForm.vue";
import CountersTable from "../components/CountersTable.vue";
import CalculationTable from "../components/CalculationTable.vue";
import { onMounted, ref, watch, onUpdated, inject } from "vue";

export default {
  components: {
    CounterForm,
    CountersTable,
    CalculationTable,
  },

  setup() {
    const show = ref(false);
    const counters = ref([]); // список счетчиков
    const totalCounters = ref();
    const searchQuery = ref("");
    const selectedCounter = ref(null);
    const showCounters = ref(true);
    const showCalc = ref(false);

    const clicked = inject("clicked", null);

    // onBeforeRouteUpdate((to, from, next) => {
    //   console.log(to.path);
    //   // Проверяем, является ли путь, по которому мы переходим, одним и тем же путем, на котором мы уже находимся
    //   if (to.path === from.path) {
    //     // Здесь можно выполнить какие-то действия, если кликнули повторно на тот же путь
    //   }

    //   // Обязательно вызываем next() в конце, чтобы продолжить нормальное выполнение маршрута
    //   next();
    // });

    watch(show, (newValue) => {
      if (newValue) {
        // Выполнить действия, когда showDialog становится true
        console.log("Модальное окно открыто");
      } else {
        // Выполнить действия, когда showDialog становится false
        console.log("Модальное окно закрыто");
      }
    });

    // функция для перезагрузки счетчиков, когда был добавлен новый счетчик
    function reloadCounters() {
      show.value = false;
      fetchData();
    }

    // Функция для загрузки счетчиков с сервера
    async function fetchData() {
      try {
        const response = await fetch("api/counters");
        const data = await response.json();
        counters.value = data;
        totalCounters.value = counters.value.length;
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    }

    onMounted(fetchData); // вызываем загрузку списка счетчиков при монтировании компонента

    onUpdated(() => {
      console.log("Компонент обновлен");
      if (clicked && clicked.value) {
        console.log("Кнопка нажата");
        clicked.value = false;
      }
    });

    const selectCounter = (counterId) => {
      selectedCounter.value = counterId;
      showCounters.value = false;
      showCalc.value = true;
    };

    return {
      show,
      counters,
      totalCounters,
      reloadCounters,
      fetchData,
      searchQuery,
      selectedCounter,
      selectCounter,
      showCounters,
      showCalc,
    };
  },
  emits: ["update:counters-clicked"],
};
</script>

<template>
  <my-dialog v-model:show="show" @close-dialog="show = false">
    <counter-form @cancel="show = false" @submitForm="reloadCounters" />
  </my-dialog>
  <div class="container">
    <div class="sidebar"></div>
    <div class="right">
      <div class="topnav">
        <div class="topnav-title">
          <template v-if="showCounters">
            <h2>Лічильники</h2>
            <div class="count indi">
              {{ totalCounters }}
            </div>
          </template>
          <template v-if="showCalc">
            <h2>Счетчик: <a href="">201302001780</a></h2>
            <h3>№ Павильона: <a href="">11/2</a></h3>
            <h3>№ Места: <span class="indi-500">20-21</span></h3>
          </template>
        </div>
        <div class="topnav-right">
          <img
            src="./icons/notify_icon.png"
            alt="notify_icon"
            width="24px"
            height="24px"
          />
          <img
            src="./icons/help_icon.png"
            alt="help_icon"
            width="24px"
            height="24px"
          />
          <img
            src="./icons/user_icon.png"
            alt="user_icon"
            width="32px"
            height="32px"
          />
        </div>
      </div>
      <div class="infobar">
        <div class="info-search">
          <div class="search-part">
            <button class="btn-tbl">
              <img src="./icons/data-table_icon.png" alt="data-table_icon" />
            </button>
            <div class="search">
              <div class="filter">
                <img
                  src="./icons/filter_icon.png"
                  alt="filter_icon"
                  width="16px"
                  height="16px"
                />
                <select name="select" id="">
                  <option value="all">All</option>
                </select>
              </div>
              <div class="search-input">
                <img
                  src="./icons/search_icon.png"
                  alt="search_icon"
                  width="16px"
                  height="16px"
                />
                <input
                  v-model="searchQuery"
                  type="text"
                  name="search"
                  id=""
                  placeholder="Search"
                />
                <img
                  src="./icons/shortcut_icon.png"
                  alt="shortcut_icon"
                  width="16px"
                  height="16px"
                />
              </div>
            </div>
          </div>

          <button
            v-if="showCounters"
            @click="show = true"
            class="btn-add ff-500-14"
          >
            + Додати лічильник
          </button>
          <button v-if="showCalc" class="btn-add ff-500-14">
            + Додати розрахунок
          </button>
        </div>

        <template v-if="showCounters">
          <div class="info-stat">
            <div class="info-item">
              <div class="info-item-top">
                <div class="info-text"><a href="">Все</a></div>
                <div class="count indi">{{ totalCounters }}</div>
              </div>
              <div class="info-select"><hr /></div>
            </div>
            <div class="info-item">
              <div class="info-item-top">
                <div class="info-text"><a href="">Общие</a></div>
                <div class="count indi">5</div>
              </div>
              <!-- <div class="info-select"><hr></div> -->
            </div>
            <div class="info-item">
              <div class="info-item-top">
                <div class="info-text"><a href="">Неактивные</a></div>
                <div class="count indi">3</div>
              </div>
              <!-- <div class="info-select"><hr></div> -->
            </div>
          </div>
        </template>

        <template v-if="showCalc">
          <div class="info-stat">
            <div class="info-item">
              <div class="info-item-top">
                <div class="info-text"><a href="">Всего начислено</a></div>
                <div class="count indi-500">100 000.00</div>
              </div>
              <div class="info-select"><hr /></div>
            </div>
            <div class="info-item">
              <div class="info-item-top">
                <div class="info-text"><a href="">Всего оплачено</a></div>
                <div class="count indi-500">99 000.00</div>
              </div>
              <!-- <div class="info-select"><hr></div> -->
            </div>
            <div class="info-item">
              <div class="info-item-top">
                <div class="info-text"><a href="">Долг</a></div>
                <div class="count red">1000.00</div>
              </div>
              <!-- <div class="info-select"><hr></div> -->
            </div>
          </div>
        </template>
      </div>

      <CountersTable
        v-if="showCounters"
        :counters="counters"
        :searchQuery="searchQuery"
        :show="show"
        @select-counter="selectCounter"
      />
      <CalculationTable
        v-if="showCalc"
        :records="records"
        :counter-id="selectedCounter"
      />
    </div>
  </div>
</template>
