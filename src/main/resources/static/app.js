const page = document.body?.dataset?.page;

const apiRequest = async (path, options = {}) => {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'same-origin',
    ...options,
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    const message = data.message || 'Ocorreu um erro. Tente novamente.';
    throw new Error(message);
  }
  return data;
};

const setMessage = (element, message, isError = false) => {
  if (!element) return;
  element.textContent = message;
  element.classList.toggle('error', isError);
  element.classList.toggle('success', !isError);
};

const handleRegister = () => {
  const form = document.getElementById('register-form');
  const message = document.getElementById('register-message');
  if (!form) return;

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(form).entries());
    try {
      const data = await apiRequest('/api/cadastro', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      setMessage(message, data.message, false);
      form.reset();
    } catch (error) {
      setMessage(message, error.message, true);
    }
  });
};

const handleLogin = () => {
  const form = document.getElementById('login-form');
  const message = document.getElementById('login-message');
  if (!form) return;

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(form).entries());
    try {
      await apiRequest('/api/entrar', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      window.location.href = '/userPage.html';
    } catch (error) {
      setMessage(message, error.message, true);
    }
  });
};

const handleDonation = () => {
  const form = document.getElementById('donation-form');
  const message = document.getElementById('donation-message');
  if (!form) return;

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(form).entries());
    try {
      const data = await apiRequest('/api/doar', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      setMessage(message, data.message, false);
      form.reset();
    } catch (error) {
      setMessage(message, error.message, true);
    }
  });
};

const handleVolunteer = () => {
  const form = document.getElementById('volunteer-form');
  const message = document.getElementById('volunteer-message');
  if (!form) return;

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(form).entries());
    try {
      const data = await apiRequest('/api/voluntario', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      setMessage(message, data.message, false);
      form.reset();
    } catch (error) {
      setMessage(message, error.message, true);
    }
  });
};

const handleUserPage = async () => {
  try {
    const data = await apiRequest('/api/usuario');
    document.getElementById('user-name').textContent = data.name;
    document.getElementById('user-cpf').textContent = data.cpf;
    document.getElementById('user-phone').textContent = data.phone;
    document.getElementById('user-points').textContent = data.points;

    const list = document.getElementById('donation-list');
    const empty = document.getElementById('donation-empty');
    list.innerHTML = '';
    if (data.donations && data.donations.length > 0) {
      empty.classList.add('hidden');
      list.classList.remove('hidden');
      data.donations.forEach((donation) => {
        const item = document.createElement('li');
        item.innerHTML = `
          <strong>${donation.description}</strong>
          <span>${donation.pickupOption}</span>
          <small>${new Date(donation.createdAt).toLocaleString('pt-BR')}</small>
        `;
        list.appendChild(item);
      });
    } else {
      empty.classList.remove('hidden');
      list.classList.add('hidden');
    }
  } catch (error) {
    window.location.href = '/entrar.html';
  }

  const logoutButton = document.getElementById('logout-secondary');
  if (logoutButton) {
    logoutButton.addEventListener('click', async () => {
      await apiRequest('/api/sair');
      window.location.href = '/';
    });
  }
};

const handleHome = async () => {
  const pointsValue = document.getElementById('points-value');
  const logoutButton = document.getElementById('logout-button');
  if (!pointsValue) return;

  try {
    const data = await apiRequest('/api/usuario');
    pointsValue.textContent = data.points;
    logoutButton?.classList.remove('hidden');
    logoutButton?.addEventListener('click', async () => {
      await apiRequest('/api/sair');
      window.location.reload();
    });
  } catch (error) {
    pointsValue.textContent = 'Cadastre-se para acumular!';
  }
};

switch (page) {
  case 'cadastro':
    handleRegister();
    break;
  case 'entrar':
    handleLogin();
    break;
  case 'doa':
    handleDonation();
    break;
  case 'voluntario':
    handleVolunteer();
    break;
  case 'usuario':
    handleUserPage();
    break;
  case 'home':
  default:
    handleHome();
    break;
}
