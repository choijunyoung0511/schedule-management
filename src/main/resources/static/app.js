const form = document.getElementById("signupForm");
const resultBox = document.getElementById("resultBox");

form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const payload = {
    username: document.getElementById("username").value.trim(),
    password: document.getElementById("password").value,
    name: document.getElementById("name").value.trim(),
    role: document.getElementById("role").value
  };

  resultBox.className = "result-box";
  resultBox.textContent = "요청 전송 중...";

  try {
    const response = await fetch("/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const raw = await response.text();
    let data;

    try {
      data = raw ? JSON.parse(raw) : {};
    } catch {
      data = raw;
    }

    if (!response.ok) {
      throw new Error(
        typeof data === "string"
          ? data
          : JSON.stringify(data)
      );
    }

    resultBox.classList.add("success");
    resultBox.textContent =
      `성공 (${response.status})\n` +
      (typeof data === "string" ? data : JSON.stringify(data, null, 2));
  } catch (error) {
    resultBox.classList.add("error");
    resultBox.textContent =
      "요청 실패\n" +
      error.message +
      "\n\nSpring Boot 서버가 실행 중인지 확인하세요.";
  }
});
