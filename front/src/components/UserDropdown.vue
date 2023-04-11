<script>
import { ref, inject } from "vue";

export default {
  setup() {
    const globalState = inject("$globalState");
    const dropdown = ref(null);
    const isDropdownVisible = ref(false);
    const menuTop = ref(0);
    const menuLeft = ref(0);

    const showDropdown = (event) => {
      console.log("showDropdown...");
      const element = event.target;
      const rect = element.getBoundingClientRect();
      menuTop.value = rect.top + rect.height;
      menuLeft.value = rect.left - rect.width * 1.75;
      isDropdownVisible.value = true;
    };

    const hideDropdown = () => {
      isDropdownVisible.value = false;
    };

    function selectUserProfile() {
      console.log("selectUserProfile");
    }

    // метод для разлогинивания пользователя
    const logout = async () => {
      const response = await fetch("/api/logout");
      if (response.ok) {
        console.log("response status: ", response.status);
        window.location.reload();
      }
      console.log("response status: ", response.status);
      window.location.reload();
    };

    return {
      dropdown,
      isDropdownVisible,
      showDropdown,
      hideDropdown,
      menuTop,
      menuLeft,
      selectUserProfile,
      username: globalState.username,
      logout,
    };
  },
};
</script>

<template>
  <div @mouseleave="hideDropdown">
    <img
      class="user-icon"
      src="./icons/user_icon.png"
      width="32"
      height="32"
      alt="User Icon"
      @mouseover="showDropdown"
    />
    <div
      class="dropdown-menu"
      :style="{
        position: 'absolute',
        top: `${menuTop}px`,
        left: `${menuLeft}px`,
      }"
      v-show="isDropdownVisible"
    >
      <div class="username ff-500-18">{{ username }}</div>
      <div class="menu-text indi ff-500-14" @click="selectUserProfile">
        {{ $translate.t("userProfile") }}
      </div>
      <div class="menu-text indi ff-500-14" @click="logout">
        {{ $translate.t("exit") }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-dropdown {
  position: relative;
  display: inline-block;
}

.user-icon {
  cursor: pointer;
}

.dropdown-menu {
  top: 0;
  right: 40;
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

.username {
  padding: 4px 10px;
}
</style>
