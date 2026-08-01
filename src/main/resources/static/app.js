const API = "";
const TOKEN_KEY = "calorieAiToken";

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => [...document.querySelectorAll(selector)];

const state = {
  token: localStorage.getItem(TOKEN_KEY),
  username: null,
  meals: [],
  today: null,
  lastAnalysis: null
};

function authHeaders(json = true) {
  const headers = {};
  if (json) headers["Content-Type"] = "application/json";
  if (state.token) headers.Authorization = `Bearer ${state.token}`;
  return headers;
}

async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, options);
  const text = await response.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      clearSession(false);
    }
    const message = typeof data === "string" && data ? data : `요청 실패 (${response.status})`;
    throw new Error(message);
  }
  return data;
}

function showToast(message, error = false) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.className = `toast${error ? " error" : ""}`;
  setTimeout(() => toast.classList.add("hidden"), 2600);
}

function openAuth(mode = "login") {
  $("#authModal").classList.remove("hidden");
  switchAuth(mode);
}

function closeAuth() {
  $("#authModal").classList.add("hidden");
  setAuthMessage("");
}

function switchAuth(mode) {
  const login = mode === "login";
  $$(".auth-tab").forEach((tab) => tab.classList.toggle("active", tab.dataset.authTab === mode));
  $("#loginForm").classList.toggle("hidden", !login);
  $("#signupForm").classList.toggle("hidden", login);
  $("#authHeading").textContent = login ? "로그인" : "회원가입";
  $("#authSubheading").textContent = login
    ? "계정에 로그인하고 식사 기록을 관리하세요."
    : "새 계정을 만들고 칼로리 기록을 시작하세요.";
  setAuthMessage("");
}

function setAuthMessage(message, error = false) {
  const box = $("#authMessage");
  if (!message) {
    box.className = "message hidden";
    box.textContent = "";
    return;
  }
  box.className = `message ${error ? "error" : "success"}`;
  box.textContent = message;
}

function setLoggedInUI(loggedIn) {
  $("#guestActions").classList.toggle("hidden", loggedIn);
  $("#userActions").classList.toggle("hidden", !loggedIn);
  $("#loginRequired").classList.toggle("hidden", loggedIn);
  $("#serviceApp").classList.toggle("hidden", !loggedIn);
  $("#headerUsername").textContent = state.username || "사용자";
  if (!loggedIn) {
    $("#recordsEmpty").textContent = "로그인하면 식사 기록을 볼 수 있습니다.";
    $("#recordsEmpty").classList.remove("hidden");
    $("#mealList").classList.add("hidden");
  }
}

function clearSession(showMessage = true) {
  state.token = null;
  state.username = null;
  state.meals = [];
  localStorage.removeItem(TOKEN_KEY);
  setLoggedInUI(false);
  renderToday({ breakfastCalories: 0, lunchCalories: 0, dinnerCalories: 0, snackCalories: 0, totalCalories: 0 });
  if (showMessage) showToast("로그아웃되었습니다.");
}

async function restoreSession() {
  if (!state.token) {
    setLoggedInUI(false);
    return;
  }
  try {
    state.username = await request("/users/me", { headers: authHeaders(false) });
    setLoggedInUI(true);
    await Promise.all([loadMeals(), loadToday()]);
  } catch {
    clearSession(false);
  }
}

async function login(username, password) {
  const data = await request("/users/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });
  const token = data?.token || data?.accessToken;
  if (!token) throw new Error("로그인 응답에 토큰이 없습니다.");
  state.token = token;
  localStorage.setItem(TOKEN_KEY, token);
  state.username = await request("/users/me", { headers: authHeaders(false) });
  setLoggedInUI(true);
  await Promise.all([loadMeals(), loadToday()]);
}

async function signup(username, password, name) {
  return request("/users", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, name, role: "USER" })
  });
}

async function createMeal(mealType, imageUrl) {
  return request("/meals", {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ mealType, imageUrl })
  });
}

async function completeAnalysis(mealId, totalCalories) {
  return request(`/meals/${mealId}/analysis`, {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ totalCalories: Number(totalCalories) })
  });
}

async function loadMeals() {
  state.meals = await request("/meals", { headers: authHeaders(false) });
  renderMeals();
  renderMealOptions();
}

async function loadToday() {
  state.today = await request("/meals/today", { headers: authHeaders(false) });
  renderToday(state.today);
}

function mealTypeLabel(type) {
  return { BREAKFAST: "아침", LUNCH: "점심", DINNER: "저녁", SNACK: "간식" }[type] || type;
}

function renderMeals() {
  const list = $("#mealList");
  const empty = $("#recordsEmpty");
  if (!state.meals.length) {
    empty.textContent = "아직 저장된 식사 기록이 없습니다.";
    empty.classList.remove("hidden");
    list.classList.add("hidden");
    list.innerHTML = "";
    return;
  }
  empty.classList.add("hidden");
  list.classList.remove("hidden");
  list.innerHTML = state.meals.map((meal) => `
    <article class="meal-item">
      <div class="meal-thumb"><span>${mealTypeLabel(meal.mealType).slice(0, 1)}</span></div>
      <div class="meal-info">
        <div><span class="meal-type">${mealTypeLabel(meal.mealType)}</span><span class="meal-id">#${meal.id}</span></div>
        <strong>${escapeHtml(meal.imageUrl)}</strong>
      </div>
      <div class="meal-status ${meal.analyzed ? "done" : "pending"}">
        <span>${meal.analyzed ? "분석 완료" : "분석 대기"}</span>
        <strong>${meal.totalCalories ?? 0} kcal</strong>
      </div>
    </article>
  `).join("");
}

function renderMealOptions() {
  const select = $("#analysisMealId");
  if (!state.meals.length) {
    select.innerHTML = '<option value="">먼저 식사 기록을 저장하세요</option>';
    select.disabled = true;
    return;
  }
  select.disabled = false;
  select.innerHTML = state.meals.map((meal) =>
    `<option value="${meal.id}">#${meal.id} · ${mealTypeLabel(meal.mealType)} · ${escapeHtml(meal.imageUrl)}</option>`
  ).join("");
}

function renderToday(data) {
  const safe = data || {};
  $("#todayBreakfast").textContent = safe.breakfastCalories ?? 0;
  $("#todayLunch").textContent = safe.lunchCalories ?? 0;
  $("#todayDinner").textContent = safe.dinnerCalories ?? 0;
  $("#todaySnack").textContent = safe.snackCalories ?? 0;
  $("#todayTotal").textContent = `${safe.totalCalories ?? 0} kcal`;
  $("#heroBreakfast").textContent = safe.breakfastCalories ?? 0;
  $("#heroLunch").textContent = safe.lunchCalories ?? 0;
  $("#heroDinner").textContent = safe.dinnerCalories ?? 0;
  $("#heroSnack").textContent = safe.snackCalories ?? 0;
  $("#heroTotal").textContent = `${safe.totalCalories ?? 0} kcal`;
  $("#heroRingValue").textContent = safe.totalCalories ?? 0;
}

function renderAnalysisResult(meal) {
  const box = $("#analysisResult");
  box.className = "result-card";
  box.innerHTML = `
    <div><span>식사 기록</span><strong>#${meal.id} · ${mealTypeLabel(meal.mealType)}</strong></div>
    <div><span>이미지</span><strong>${escapeHtml(meal.imageUrl)}</strong></div>
    <div><span>총칼로리</span><strong>${meal.totalCalories} kcal</strong></div>
    <div><span>분석 상태</span><strong>완료</strong></div>
  `;
}

function showStep(step) {
  $$(".step").forEach((button) => button.classList.toggle("active", Number(button.dataset.step) === step));
  for (let i = 1; i <= 4; i += 1) {
    $(`#step${i}Panel`).classList.toggle("hidden", i !== step);
  }
  if (step === 4 && state.token) loadToday().catch((e) => showToast(e.message, true));
}

function escapeHtml(value = "") {
  return String(value).replace(/[&<>'"]/g, (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;" }[char]));
}

$$('[data-open-auth]').forEach((button) => button.addEventListener("click", () => openAuth(button.dataset.openAuth)));
$$(".auth-tab").forEach((button) => button.addEventListener("click", () => switchAuth(button.dataset.authTab)));
$$(".step").forEach((button) => button.addEventListener("click", () => showStep(Number(button.dataset.step))));

$("#closeModalBtn").addEventListener("click", closeAuth);
$("#authModal").addEventListener("click", (event) => { if (event.target.id === "authModal") closeAuth(); });
$("#logoutBtn").addEventListener("click", () => clearSession(true));
$("#heroStartBtn").addEventListener("click", () => state.token ? document.querySelector("#service").scrollIntoView({ behavior: "smooth" }) : openAuth("login"));
$("#goResultBtn").addEventListener("click", () => showStep(4));
$("#refreshRecordsBtn").addEventListener("click", async () => {
  if (!state.token) return openAuth("login");
  try { await Promise.all([loadMeals(), loadToday()]); showToast("기록을 새로 불러왔습니다."); } catch (e) { showToast(e.message, true); }
});

$("#loginForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  setAuthMessage("로그인 중입니다...");
  try {
    await login($("#loginUsername").value.trim(), $("#loginPassword").value);
    closeAuth();
    showToast("로그인되었습니다.");
  } catch (error) { setAuthMessage(error.message, true); }
});

$("#signupForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const password = $("#signupPassword").value;
  if (password !== $("#signupPasswordConfirm").value) return setAuthMessage("비밀번호가 일치하지 않습니다.", true);
  setAuthMessage("회원가입 요청 중입니다...");
  try {
    await signup($("#signupUsername").value.trim(), password, $("#signupName").value.trim());
    setAuthMessage("회원가입이 완료되었습니다. 로그인해 주세요.");
    switchAuth("login");
    $("#loginUsername").value = $("#signupUsername").value.trim();
  } catch (error) { setAuthMessage(error.message, true); }
});

$("#mealCreateForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const meal = await createMeal($("#mealType").value, $("#imageUrl").value.trim());
    $("#imageUrl").value = "";
    await loadMeals();
    showToast(`식사 기록 #${meal.id}이 저장되었습니다.`);
    showStep(2);
  } catch (error) { showToast(error.message, true); }
});

$("#analysisForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const meal = await completeAnalysis($("#analysisMealId").value, $("#analysisCalories").value);
    state.lastAnalysis = meal;
    renderAnalysisResult(meal);
    await Promise.all([loadMeals(), loadToday()]);
    showToast("칼로리 분석 결과가 반영되었습니다.");
    showStep(3);
  } catch (error) { showToast(error.message, true); }
});

restoreSession();
