import { ref, readonly } from "vue";

const username = ref(null);

function setUsername(newUsername) {
  username.value = newUsername;
}

export default function useGlobalState() {
  return {
    username: readonly(username),
    setUsername,
  };
}
