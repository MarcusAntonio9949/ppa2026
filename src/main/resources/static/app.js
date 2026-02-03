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

const getStoredUserId = () => localStorage.getItem('userId');
const getStoredAdmin = () => localStorage.getItem('isAdmin') === 'true';

const setStoredUserId = (userId) => {
  if (userId) {
    localStorage.setItem('userId', String(userId));
  }
};

const clearStoredUserId = () => localStorage.removeItem('userId');
const setStoredAdmin = (value) => localStorage.setItem('isAdmin', String(Boolean(value)));
const clearStoredAdmin = () => localStorage.removeItem('isAdmin');

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
      setMessage(message, 'Cadastrado com sucesso', false);
      form.reset();
      setTimeout(() => {
        window.location.href = '/index.html';
      }, 2000);
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
      const data = await apiRequest('/api/entrar', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      if (data.admin) {
        setStoredAdmin(true);
        window.location.href = '/admin.html';
        return;
      }
      setStoredUserId(data.id);
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
    const storedUserId = getStoredUserId();
    if (storedUserId) {
      payload.userId = Number(storedUserId);
    }
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
    const storedUserId = getStoredUserId();
    const userPath = storedUserId ? `/api/usuario?userId=${storedUserId}` : '/api/usuario';
    const data = await apiRequest(userPath);
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
      clearStoredUserId();
      clearStoredAdmin();
      window.location.href = '/';
    });
  }
};

const handleHome = async () => {
  const pointsValue = document.getElementById('points-value');
  const logoutButton = document.getElementById('logout-button');
  if (!pointsValue) return;

  try {
    const storedUserId = getStoredUserId();
    const userPath = storedUserId ? `/api/usuario?userId=${storedUserId}` : '/api/usuario';
    const data = await apiRequest(userPath);
    pointsValue.textContent = data.points;
    logoutButton?.classList.remove('hidden');
    logoutButton?.addEventListener('click', async () => {
      await apiRequest('/api/sair');
      clearStoredUserId();
      clearStoredAdmin();
      window.location.reload();
    });
  } catch (error) {
    pointsValue.textContent = 'Cadastre-se para acumular!';
  }
};

const handleAdminPage = async () => {
  const adminFlag = getStoredAdmin();
  try {
    const overviewPath = adminFlag ? '/api/admin/overview?admin=true' : '/api/admin/overview';
    const data = await apiRequest(overviewPath);
    const usersList = document.getElementById('admin-users');
    const donationsList = document.getElementById('admin-donations');
    const volunteersList = document.getElementById('admin-volunteers');

    usersList.innerHTML = '';
    data.users.forEach((user) => {
      const item = document.createElement('li');
      item.textContent = `${user.id} - ${user.name} (CPF: ${user.cpf}) | Tel: ${user.phone} | Pontos: ${user.points}`;
      usersList.appendChild(item);
    });

    donationsList.innerHTML = '';
    data.donations.forEach((donation) => {
      const item = document.createElement('li');
      item.textContent = `${donation.description} - ${donation.pickupOption} (${donation.createdAt})`;
      donationsList.appendChild(item);
    });

    volunteersList.innerHTML = '';
    data.volunteers.forEach((volunteer) => {
      const item = document.createElement('li');
      item.textContent = `${volunteer.name} (${volunteer.email}) | Tel: ${volunteer.phone} | ${volunteer.availability}`;
      volunteersList.appendChild(item);
    });
  } catch (error) {
    window.location.href = '/entrar.html';
  }

  const adminLogout = document.getElementById('admin-logout');
  if (adminLogout) {
    adminLogout.addEventListener('click', async () => {
      await apiRequest('/api/sair');
      clearStoredAdmin();
      clearStoredUserId();
      window.location.href = '/index.html';
    });
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
  case 'admin':
    handleAdminPage();
    break;
}
