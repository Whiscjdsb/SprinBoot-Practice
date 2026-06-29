/**
 * 用户管理系统 — Vue 3 应用
 * 极简现代高级感设计 · 对接 Spring Boot REST API
 * 后端不可用时自动降级到本地 Mock 数据
 */

const { createApp, ref, reactive, computed, onMounted, nextTick, getCurrentInstance } = Vue;

/* ============================================================
   API 基础路径
   ============================================================ */
const API_BASE = '/api/users';

/* ============================================================
   Mock 数据存储（后端不可用时启用）
   ============================================================ */
let mockUsers = [
    { id: 1, name: '张三', age: 25 },
    { id: 2, name: '李四', age: 30 },
    { id: 3, name: '王五', age: 28 },
    { id: 4, name: '赵六', age: 35 },
    { id: 5, name: '孙七', age: 22 },
    { id: 6, name: '周八', age: 40 },
    { id: 7, name: '吴九', age: 33 },
    { id: 8, name: '郑十', age: 27 },
    { id: 9, name: '钱多多', age: 45 },
    { id: 10, name: '冯小宝', age: 19 },
    { id: 11, name: '陈大大', age: 52 },
    { id: 12, name: '褚小小', age: 16 },
];
let mockId = 13;

/**
 * Mock API 实现 — 模拟后端接口
 */
function handleMock(method, path, body) {
    return new Promise(resolve => {
        setTimeout(() => {
            // 分页查询
            if (method === 'GET' && path.startsWith('/page?')) {
                const params = new URLSearchParams(path.replace('/page?', ''));
                const page = Math.max(1, parseInt(params.get('page') || '1'));
                const size = parseInt(params.get('size') || '10');
                const name = params.get('name') || '';
                const age = params.get('age');

                let list = [...mockUsers];
                if (name) list = list.filter(u => u.name.includes(name));
                if (age !== null && age !== undefined && age !== '') {
                    list = list.filter(u => u.age === parseInt(age));
                }

                const total = list.length;
                const pages = Math.max(1, Math.ceil(total / size));
                const start = (page - 1) * size;

                resolve({
                    code: 200,
                    message: 'ok',
                    data: {
                        list: list.slice(start, start + size),
                        total,
                        page,
                        size,
                        pages,
                    },
                });
                return;
            }

            // 获取单条
            const getMatch = path.match(/^\/(\d+)$/);
            if (method === 'GET' && getMatch) {
                const user = mockUsers.find(u => u.id === parseInt(getMatch[1]));
                return resolve(user ? { code: 200, message: 'ok', data: { ...user } }
                                  : { code: 404, message: '用户不存在', data: null });
            }

            // 新增
            if (method === 'POST' && path === '/') {
                const newUser = { id: mockId++, name: body.name, age: body.age };
                mockUsers.push(newUser);
                return resolve({ code: 200, message: 'ok', data: { ...newUser } });
            }

            // 更新
            if (method === 'PUT' && getMatch) {
                const user = mockUsers.find(u => u.id === parseInt(getMatch[1]));
                if (!user) return resolve({ code: 404, message: '用户不存在', data: null });
                user.name = body.name;
                user.age = body.age;
                return resolve({ code: 200, message: 'ok', data: { ...user } });
            }

            // 删除
            if (method === 'DELETE' && getMatch) {
                const idx = mockUsers.findIndex(u => u.id === parseInt(getMatch[1]));
                if (idx === -1) return resolve({ code: 404, message: '用户不存在', data: null });
                mockUsers.splice(idx, 1);
                return resolve({ code: 200, message: 'ok', data: null });
            }

            resolve({ code: 400, message: '未知请求', data: null });
        }, 280);
    });
}

/**
 * 统一请求方法 — 优先真实后端，失败自动降级 Mock
 */
async function request(method, path, body = null) {
    const url = API_BASE + path;
    try {
        const opts = {
            method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (body && method !== 'GET') opts.body = JSON.stringify(body);
        const res = await fetch(url, opts);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const json = await res.json();
        // 成功后确保模式为 live
        if (vm && vm.apiMode !== 'live') vm.apiMode = 'live';
        return json;
    } catch {
        // 降级 Mock
        if (vm && vm.apiMode !== 'mock') vm.apiMode = 'mock';
        return await handleMock(method, path, body);
    }
}

/* ============================================================
   头像渐变色盘 — 10 种柔和蓝紫渐变
   ============================================================ */
const AVATAR_GRADIENTS = [
    'linear-gradient(135deg, #6366f1, #a855f7)',
    'linear-gradient(135deg, #818cf8, #c084fc)',
    'linear-gradient(135deg, #7c3aed, #a78bfa)',
    'linear-gradient(135deg, #6366f1, #ec4899)',
    'linear-gradient(135deg, #14b8a6, #6366f1)',
    'linear-gradient(135deg, #ec4899, #a855f7)',
    'linear-gradient(135deg, #0ea5e9, #818cf8)',
    'linear-gradient(135deg, #a855f7, #ec4899)',
    'linear-gradient(135deg, #6366f1, #f59e0b)',
    'linear-gradient(135deg, #14b8a6, #a855f7)',
];

function getAvatarColor(name) {
    if (!name) return AVATAR_GRADIENTS[0];
    const code = name.charCodeAt(0) || 0;
    return AVATAR_GRADIENTS[code % AVATAR_GRADIENTS.length];
}

function getInitial(name) {
    if (!name) return '?';
    return name.charAt(0).toUpperCase();
}

/* ============================================================
   年龄分组
   ============================================================ */
function getAgeGroup(age) {
    if (age < 18) return 'young';
    if (age < 30) return 'adult';
    if (age < 45) return 'middle';
    return 'senior';
}

/* ============================================================
   Vue 应用
   ============================================================ */
let vm = null; // 全局引用，供 request() 内使用

const app = createApp({
    setup() {
        // ---- 状态 ----
        const users = ref([]);
        const loading = ref(false);
        const submitting = ref(false);
        const apiMode = ref('live'); // 'live' | 'mock'

        const searchName = ref('');
        const searchAge = ref(null);

        const pagination = reactive({
            page: 1,
            size: 10,
            total: 0,
            pages: 0,
        });

        // 模态框
        const showModal = ref(false);
        const isEdit = ref(false);
        const editingId = ref(null);
        const form = reactive({ name: '', age: null });
        const errors = reactive({ name: '', age: '' });
        const nameInput = ref(null);

        // 删除确认
        const showDeleteConfirm = ref(false);
        const deleteTarget = ref(null);

        // Toast
        const toast = reactive({
            show: false,
            type: 'success', // 'success' | 'error' | 'info'
            message: '',
            _timer: null,
        });

        // ---- 计算属性 ----
        const totalCount = computed(() => pagination.total);
        const hasSearchFilter = computed(() => !!(searchName.value || (searchAge.value !== null && searchAge.value !== '')));

        const displayPages = computed(() => {
            const cur = pagination.page;
            const tot = pagination.pages;
            if (tot <= 7) return Array.from({ length: tot }, (_, i) => i + 1);
            const pages = [1];
            if (cur > 3) pages.push('...');
            const start = Math.max(2, cur - 1);
            const end = Math.min(tot - 1, cur + 1);
            for (let i = start; i <= end; i++) pages.push(i);
            if (cur < tot - 2) pages.push('...');
            pages.push(tot);
            return pages;
        });

        // ---- Toast 方法 ----
        function showToast(type, msg) {
            if (toast._timer) clearTimeout(toast._timer);
            toast.type = type;
            toast.message = msg;
            toast.show = true;
            toast._timer = setTimeout(() => { toast.show = false; }, 2800);
        }

        // ---- 数据加载 ----
        async function loadUsers() {
            loading.value = true;
            try {
                const params = new URLSearchParams();
                params.set('page', pagination.page);
                params.set('size', pagination.size);
                if (searchName.value) params.set('name', searchName.value);
                if (searchAge.value !== null && searchAge.value !== '') {
                    params.set('age', searchAge.value);
                }
                const res = await request('GET', `/page?${params.toString()}`);
                if (res.code === 200 && res.data) {
                    users.value = res.data.list || [];
                    pagination.total = res.data.total || 0;
                    pagination.pages = res.data.pages || 0;
                    pagination.page = res.data.page || 1;
                } else {
                    showToast('error', res.message || '加载失败');
                    users.value = [];
                }
            } catch {
                showToast('error', '网络异常，请稍后重试');
                users.value = [];
            } finally {
                loading.value = false;
            }
        }

        // ---- 搜索 ----
        function handleSearch() {
            pagination.page = 1;
            loadUsers();
        }

        function resetSearch() {
            searchName.value = '';
            searchAge.value = null;
            pagination.page = 1;
            loadUsers();
        }

        // ---- 分页 ----
        function goToPage(p) {
            if (p < 1 || p > pagination.pages || p === pagination.page) return;
            pagination.page = p;
            loadUsers();
        }

        function handlePageSizeChange() {
            pagination.page = 1;
            loadUsers();
        }

        // ---- 新增 ----
        function openCreateModal() {
            isEdit.value = false;
            editingId.value = null;
            form.name = '';
            form.age = null;
            errors.name = '';
            errors.age = '';
            showModal.value = true;
            nextTick(() => {
                nameInput.value?.focus();
            });
        }

        // ---- 编辑 ----
        function openEditModal(user) {
            isEdit.value = true;
            editingId.value = user.id;
            form.name = user.name;
            form.age = user.age;
            errors.name = '';
            errors.age = '';
            showModal.value = true;
            nextTick(() => {
                nameInput.value?.focus();
            });
        }

        function closeModal() {
            showModal.value = false;
        }

        // ---- 表单验证 ----
        function validate() {
            let ok = true;
            errors.name = '';
            errors.age = '';
            if (!form.name || !form.name.trim()) {
                errors.name = '姓名不能为空';
                ok = false;
            } else if (form.name.length > 50) {
                errors.name = '姓名不超过 50 个字符';
                ok = false;
            }
            if (form.age === null || form.age === '' || form.age === undefined) {
                errors.age = '年龄不能为空';
                ok = false;
            } else if (form.age < 0 || form.age > 120) {
                errors.age = '年龄范围为 0-120';
                ok = false;
            }
            return ok;
        }

        // ---- 提交 ----
        async function submitForm() {
            if (!validate()) return;
            submitting.value = true;
            try {
                const payload = { name: form.name.trim(), age: parseInt(form.age) };
                if (isEdit.value) {
                    const res = await request('PUT', `/${editingId.value}`, payload);
                    if (res.code === 200) {
                        showToast('success', '用户信息已更新');
                        closeModal();
                        loadUsers();
                    } else {
                        showToast('error', res.message || '更新失败');
                    }
                } else {
                    const res = await request('POST', '/', payload);
                    if (res.code === 200) {
                        showToast('success', '用户创建成功');
                        closeModal();
                        loadUsers();
                    } else {
                        showToast('error', res.message || '创建失败');
                    }
                }
            } catch {
                showToast('error', '操作失败，请稍后重试');
            } finally {
                submitting.value = false;
            }
        }

        // ---- 删除 ----
        function confirmDelete(user) {
            deleteTarget.value = user;
            showDeleteConfirm.value = true;
        }

        function cancelDelete() {
            showDeleteConfirm.value = false;
            deleteTarget.value = null;
        }

        async function handleDelete() {
            if (!deleteTarget.value) return;
            submitting.value = true;
            try {
                const res = await request('DELETE', `/${deleteTarget.value.id}`);
                if (res.code === 200) {
                    showToast('success', `用户「${deleteTarget.value.name}」已删除`);
                    cancelDelete();
                    if (users.value.length === 1 && pagination.page > 1) {
                        pagination.page--;
                    }
                    loadUsers();
                } else {
                    showToast('error', res.message || '删除失败');
                }
            } catch {
                showToast('error', '删除失败，请稍后重试');
            } finally {
                submitting.value = false;
            }
        }

        // ---- 模式切换 ----
        function toggleApiMode() {
            if (apiMode.value === 'mock') {
                showToast('info', '正在尝试连接后端…');
                loadUsers();
            }
        }

        // ---- 初始化 ----
        onMounted(() => {
            loadUsers();
        });

        // ---- 暴露给模板 ----
        return {
            users,
            loading,
            submitting,
            apiMode,
            searchName,
            searchAge,
            pagination,
            showModal,
            isEdit,
            form,
            errors,
            nameInput,
            showDeleteConfirm,
            deleteTarget,
            toast,
            totalCount,
            hasSearchFilter,
            displayPages,
            handleSearch,
            resetSearch,
            goToPage,
            handlePageSizeChange,
            openCreateModal,
            openEditModal,
            closeModal,
            submitForm,
            confirmDelete,
            cancelDelete,
            handleDelete,
            getAvatarColor,
            getAgeGroup,
            getInitial,
            toggleApiMode,
            loadUsers,
        };
    },
});

vm = app.mount('#app');
