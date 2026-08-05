const API = "";
const TOKEN_KEY = "calorieAiToken";

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => [...document.querySelectorAll(selector)];

const state = {
  token: localStorage.getItem(TOKEN_KEY),
  username: null,
  meals: [],
  today: null,
  analyzing: false
};

function authHeaders(json = true) {
  const headers = {};

  if (json) {
    headers["Content-Type"] = "application/json";
  }

  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }

  return headers;
}

async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, options);
  const text = await response.text();

  let data = null;

  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      clearSession(false);
    }

    const message =
      typeof data === "string" && data
        ? data
        : data?.message || `요청 실패 (${response.status})`;

    throw new Error(message);
  }

  return data;
}

function showToast(message, error = false) {
  const toast = $("#toast");

  toast.textContent = message;
  toast.className = `toast${error ? " error" : ""}`;

  window.clearTimeout(showToast.timer);

  showToast.timer = window.setTimeout(() => {
    toast.classList.add("hidden");
  }, 2800);
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
  const loginMode = mode === "login";

  $$(".auth-tab").forEach((tab) => {
    tab.classList.toggle("active", tab.dataset.authTab === mode);
  });

  $("#loginForm").classList.toggle("hidden", !loginMode);
  $("#signupForm").classList.toggle("hidden", loginMode);

  $("#authHeading").textContent = loginMode ? "로그인" : "회원가입";
  $("#authSubheading").textContent = loginMode
    ? "계정에 로그인하고 음식 사진을 분석하세요."
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
    $("#mealList").innerHTML = "";
    resetResult();
  }
}

function clearSession(showMessage = true) {
  state.token = null;
  state.username = null;
  state.meals = [];
  state.today = null;

  localStorage.removeItem(TOKEN_KEY);

  setLoggedInUI(false);
  renderToday({
    breakfastCalories: 0,
    lunchCalories: 0,
    dinnerCalories: 0,
    snackCalories: 0,
    totalCalories: 0
  });

  if (showMessage) {
    showToast("로그아웃되었습니다.");
  }
}

async function restoreSession() {
  if (!state.token) {
    setLoggedInUI(false);
    return;
  }

  try {
    state.username = await request("/users/me", {
      headers: authHeaders(false)
    });

    setLoggedInUI(true);

    await Promise.all([loadMeals(), loadToday()]);
  } catch {
    clearSession(false);
  }
}

async function login(username, password) {
  const data = await request("/users/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      username,
      password
    })
  });

  const token = data?.token || data?.accessToken;

  if (!token) {
    throw new Error("로그인 응답에 JWT 토큰이 없습니다.");
  }

  state.token = token;
  localStorage.setItem(TOKEN_KEY, token);

  state.username = await request("/users/me", {
    headers: authHeaders(false)
  });

  setLoggedInUI(true);

  await Promise.all([loadMeals(), loadToday()]);
}

async function signup(username, password, name) {
  return request("/users", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      username,
      password,
      name,
      role: "USER"
    })
  });
}

async function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  return request("/files/images", {
    method: "POST",
    headers: authHeaders(false),
    body: formData
  });
}

async function analyzeFoodImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  return request("/gemini/analyze-image", {
    method: "POST",
    headers: authHeaders(false),
    body: formData
  });
}

async function createMeal(mealType, imageUrl) {
  return request("/meals", {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({
      mealType,
      imageUrl
    })
  });
}

async function completeAnalysis(mealId, totalCalories) {
  return request(`/meals/${mealId}/analysis`, {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({
      totalCalories: Number(totalCalories)
    })
  });
}

async function analyzeAndSaveMeal(file, mealType) {
  const [uploadResult, analysisResult] = await Promise.all([
    uploadImage(file),
    analyzeFoodImage(file)
  ]);

  if (!uploadResult?.imageUrl) {
    throw new Error("업로드 응답에 imageUrl이 없습니다.");
  }

  if (
    !analysisResult?.foodName ||
    analysisResult?.estimatedCalories === undefined ||
    analysisResult?.estimatedCalories === null
  ) {
    throw new Error("AI 분석 응답 형식이 올바르지 않습니다.");
  }

  const meal = await createMeal(mealType, uploadResult.imageUrl);

  const completedMeal = await completeAnalysis(
    meal.id,
    analysisResult.estimatedCalories
  );

  return {
    meal: completedMeal,
    analysis: analysisResult,
    imageUrl: uploadResult.imageUrl
  };
}

async function loadMeals() {
  state.meals = await request("/meals", {
    headers: authHeaders(false)
  });

  renderMeals();
}

async function loadToday() {
  state.today = await request("/meals/today", {
    headers: authHeaders(false)
  });

  renderToday(state.today);
}

function mealTypeLabel(type) {
  return {
    BREAKFAST: "아침",
    LUNCH: "점심",
    DINNER: "저녁",
    SNACK: "간식"
  }[type] || type;
}

function imageSource(imageUrl) {
  if (!imageUrl) {
    return "";
  }

  if (
    imageUrl.startsWith("http://") ||
    imageUrl.startsWith("https://") ||
    imageUrl.startsWith("/")
  ) {
    return imageUrl;
  }

  return `/${imageUrl}`;
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

  list.innerHTML = state.meals
    .map((meal) => {
      const src = imageSource(meal.imageUrl);

      return `
        <article class="meal-item">
          <div class="meal-thumb">
            ${
              src
                ? `<img src="${escapeHtml(src)}" alt="식사 이미지" />`
                : `<span>${mealTypeLabel(meal.mealType).slice(0, 1)}</span>`
            }
          </div>

          <div class="meal-info">
            <div>
              <span class="meal-type">${mealTypeLabel(meal.mealType)}</span>
              <span class="meal-id">#${meal.id}</span>
            </div>
            <strong>${escapeHtml(meal.imageUrl || "이미지 없음")}</strong>
          </div>

          <div class="meal-status ${meal.analyzed ? "done" : "pending"}">
            <span>${meal.analyzed ? "분석 완료" : "분석 대기"}</span>
            <strong>${meal.totalCalories ?? 0} kcal</strong>
          </div>
        </article>
      `;
    })
    .join("");
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

function renderAiResult(result) {
  const box = $("#aiResult");
  const analysis = result.analysis;
  const meal = result.meal;

  box.className = "ai-result";
  box.innerHTML = `
    <div class="ai-result-head">
      <span>AI 분석 완료</span>
      <strong>${escapeHtml(analysis.foodName)}</strong>
    </div>

    <div class="ai-result-grid">
      <div>
        <span>예상 칼로리</span>
        <strong>${analysis.estimatedCalories} kcal</strong>
      </div>

      <div>
        <span>식사 구분</span>
        <strong>${mealTypeLabel(meal.mealType)}</strong>
      </div>

      <div>
        <span>기록 번호</span>
        <strong>#${meal.id}</strong>
      </div>

      <div>
        <span>저장 상태</span>
        <strong>Meal 저장 완료</strong>
      </div>
    </div>

    <div class="ai-description">
      <span>분석 설명</span>
      <p>${escapeHtml(analysis.description || "설명이 없습니다.")}</p>
    </div>

    <div class="ai-warning">
      AI 분석 결과는 사진을 기반으로 한 추정치입니다. 실제 양과 재료에 따라 달라질 수 있습니다.
    </div>
  `;
}

function resetResult() {
  const box = $("#aiResult");

  box.className = "ai-result empty";
  box.innerHTML = `
    <div class="result-placeholder">
      <span class="result-icon">✨</span>
      <strong>아직 분석 결과가 없습니다.</strong>
      <p>사진을 선택하고 AI 분석을 실행해 주세요.</p>
    </div>
  `;
}

function setAnalyzing(analyzing) {
  state.analyzing = analyzing;

  const button = $("#analyzeBtn");
  const input = $("#foodImage");
  const select = $("#mealType");

  button.disabled = analyzing;
  input.disabled = analyzing;
  select.disabled = analyzing;

  button.textContent = analyzing
    ? "AI가 사진을 분석하고 저장하는 중..."
    : "AI 분석하고 저장하기";
}

function escapeHtml(value = "") {
  return String(value).replace(
    /[&<>'"]/g,
    (char) =>
      ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        "'": "&#39;",
        '"': "&quot;"
      })[char]
  );
}

$$("[data-open-auth]").forEach((button) => {
  button.addEventListener("click", () => {
    openAuth(button.dataset.openAuth);
  });
});

$$(".auth-tab").forEach((button) => {
  button.addEventListener("click", () => {
    switchAuth(button.dataset.authTab);
  });
});

$("#closeModalBtn").addEventListener("click", closeAuth);

$("#authModal").addEventListener("click", (event) => {
  if (event.target.id === "authModal") {
    closeAuth();
  }
});

$("#logoutBtn").addEventListener("click", () => {
  clearSession(true);
});

$("#heroStartBtn").addEventListener("click", () => {
  if (state.token) {
    $("#service").scrollIntoView({
      behavior: "smooth"
    });
  } else {
    openAuth("login");
  }
});

$("#refreshDashboardBtn").addEventListener("click", async () => {
  if (!state.token) {
    openAuth("login");
    return;
  }

  try {
    await Promise.all([loadToday(), loadMeals()]);
    showToast("대시보드를 새로 불러왔습니다.");
  } catch (error) {
    showToast(error.message, true);
  }
});

$("#refreshRecordsBtn").addEventListener("click", async () => {
  if (!state.token) {
    openAuth("login");
    return;
  }

  try {
    await loadMeals();
    showToast("식사 기록을 새로 불러왔습니다.");
  } catch (error) {
    showToast(error.message, true);
  }
});

$("#loginForm").addEventListener("submit", async (event) => {
  event.preventDefault();

  setAuthMessage("로그인 중입니다...");

  try {
    await login(
      $("#loginUsername").value.trim(),
      $("#loginPassword").value
    );

    closeAuth();
    showToast("로그인되었습니다.");

    $("#service").scrollIntoView({
      behavior: "smooth"
    });
  } catch (error) {
    setAuthMessage(error.message, true);
  }
});

$("#signupForm").addEventListener("submit", async (event) => {
  event.preventDefault();

  const password = $("#signupPassword").value;
  const passwordConfirm = $("#signupPasswordConfirm").value;

  if (password !== passwordConfirm) {
    setAuthMessage("비밀번호가 일치하지 않습니다.", true);
    return;
  }

  setAuthMessage("회원가입 요청 중입니다...");

  try {
    const username = $("#signupUsername").value.trim();

    await signup(
      username,
      password,
      $("#signupName").value.trim()
    );

    switchAuth("login");
    $("#loginUsername").value = username;
    setAuthMessage("회원가입이 완료되었습니다. 로그인해 주세요.");
  } catch (error) {
    setAuthMessage(error.message, true);
  }
});

$("#foodImage").addEventListener("change", () => {
  const file = $("#foodImage").files[0];
  const previewWrap = $("#imagePreviewWrap");
  const preview = $("#imagePreview");

  if (!file) {
    previewWrap.classList.add("hidden");
    preview.removeAttribute("src");
    return;
  }

  preview.src = URL.createObjectURL(file);
  previewWrap.classList.remove("hidden");
  resetResult();
});

$("#aiMealForm").addEventListener("submit", async (event) => {
  event.preventDefault();

  if (state.analyzing) {
    return;
  }

  if (!state.token) {
    openAuth("login");
    return;
  }

  const file = $("#foodImage").files[0];
  const mealType = $("#mealType").value;

  if (!file) {
    showToast("음식 사진을 선택해 주세요.", true);
    return;
  }

  setAnalyzing(true);
  resetResult();

  try {
    const result = await analyzeAndSaveMeal(file, mealType);

    renderAiResult(result);

    await Promise.all([loadMeals(), loadToday()]);

    showToast(
      `${result.analysis.foodName} 분석과 저장이 완료되었습니다.`
    );
  } catch (error) {
    showToast(error.message, true);
  } finally {
    setAnalyzing(false);
  }
});

restoreSession();
