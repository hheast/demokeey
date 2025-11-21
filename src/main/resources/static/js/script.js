// 本地存储管理
const STORAGE_KEYS = {
    CHAT_SESSIONS: 'doubao_chat_sessions',
    CURRENT_CHAT_ID: 'doubao_current_chat_id',
    CURRENT_SKILL: 'doubao_current_skill',
    CURRENT_USER: 'doubao_current_user'
};

// 存储聊天数据
let chatSessions = {};
let currentChatId = null;
let pendingImages = [];
let currentSkill = null;
let allSkills = [];
let isProcessing = false;
let isLoggedIn = false;
let currentUser = null;
let vipPoints = 1250;

// 初始化应用
document.addEventListener('DOMContentLoaded', function() {
    initApp();
});

// 初始化应用
async function initApp() {
    try {
// 检查登录状态
        checkLoginStatus();

// 从本地存储加载数据
        loadFromLocalStorage();

// 加载技能列表
        await loadSkills();

// 如果没有聊天会话，创建一个新的
        if (Object.keys(chatSessions).length === 0) {
            await createNewChat();
        } else {
// 切换到当前聊天会话
            await switchToChat(currentChatId);
        }

// 绑定事件
        bindEvents();

        console.log('应用初始化完成');
    } catch (error) {
        console.error('应用初始化失败:', error);
    }
}

// 检查登录状态
function checkLoginStatus() {
    const savedUser = localStorage.getItem(STORAGE_KEYS.CURRENT_USER);

    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        isLoggedIn = true;
        updateUserInterface();
    }
}

// 从本地存储加载数据
function loadFromLocalStorage() {
    try {
// 加载聊天会话
        const savedSessions = localStorage.getItem(STORAGE_KEYS.CHAT_SESSIONS);
        if (savedSessions) {
            chatSessions = JSON.parse(savedSessions);
        }

// 加载当前聊天ID
        const savedChatId = localStorage.getItem(STORAGE_KEYS.CURRENT_CHAT_ID);
        if (savedChatId && chatSessions[savedChatId]) {
            currentChatId = savedChatId;
        }

// 加载当前技能
        const savedSkill = localStorage.getItem(STORAGE_KEYS.CURRENT_SKILL);
        if (savedSkill) {
            currentSkill = JSON.parse(savedSkill);
        }

        console.log('从本地存储加载数据完成');
    } catch (error) {
        console.error('从本地存储加载数据失败:', error);
    }
}

// 保存数据到本地存储
function saveToLocalStorage() {
    try {
        localStorage.setItem(STORAGE_KEYS.CHAT_SESSIONS, JSON.stringify(chatSessions));
        localStorage.setItem(STORAGE_KEYS.CURRENT_CHAT_ID, currentChatId);

        if (currentSkill) {
            localStorage.setItem(STORAGE_KEYS.CURRENT_SKILL, JSON.stringify(currentSkill));
        }

        if (currentUser) {
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser));
        }

        console.log('数据已保存到本地存储');
    } catch (error) {
        console.error('保存到本地存储失败:', error);
    }
}

// 清理所有聊天记录
function clearAllChatSessions() {
    chatSessions = {};
    currentChatId = null;
    currentSkill = null;

    localStorage.removeItem(STORAGE_KEYS.CHAT_SESSIONS);
    localStorage.removeItem(STORAGE_KEYS.CURRENT_CHAT_ID);
    localStorage.removeItem(STORAGE_KEYS.CURRENT_SKILL);

// 重新初始化应用
    initApp();
}

// 更新用户界面状态
function updateUserInterface() {
    const userNameElement = document.getElementById('userName');
    const userStatusElement = document.getElementById('userStatus');
    const messageInput = document.getElementById('messageInput');

    if (isLoggedIn && currentUser) {
        userNameElement.textContent = currentUser.username;
        userStatusElement.textContent = 'VIP会员';
        messageInput.placeholder = '请先选择一个技能，然后输入内容';
    } else {
        userNameElement.textContent = '未登录';
        userStatusElement.textContent = '点击登录';
        messageInput.placeholder = '请先登录并选择技能';
    }
}

// 显示登录模态框
function showLoginModal() {
    document.getElementById('loginModal').classList.add('active');
}

// 隐藏登录模态框
function hideLoginModal() {
    document.getElementById('loginModal').classList.remove('active');
}

// 处理登录
async function handleLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if (!username || !password) {
        alert('请输入用户名和密码');
        return;
    }

// 模拟登录成功
    isLoggedIn = true;
    currentUser = {
        id: 'user_' + Date.now(),
        username: username,
        email: username + '@example.com'
    };

// 保存用户信息
    localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(currentUser));

// 更新界面
    updateUserInterface();
    hideLoginModal();

    alert('登录成功！');
}

// 处理注册
async function handleRegister() {
    const username = document.getElementById('registerUsername').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value.trim();
    const confirmPassword = document.getElementById('confirmPassword').value.trim();

    if (!username || !email || !password || !confirmPassword) {
        alert('请填写所有字段');
        return;
    }

    if (password !== confirmPassword) {
        alert('两次输入的密码不一致');
        return;
    }

    if (password.length < 6) {
        alert('密码长度至少6位');
        return;
    }

// 模拟注册成功
    alert('注册成功！请登录');

// 切换到登录标签
    document.querySelector('.login-tab[data-tab="login"]').click();

// 清空注册表单
    document.getElementById('registerUsername').value = '';
    document.getElementById('registerEmail').value = '';
    document.getElementById('registerPassword').value = '';
    document.getElementById('confirmPassword').value = '';
}

// 处理QQ登录
function handleQQLogin() {
    alert('QQ登录功能需要后端支持，当前为演示模式');
}

// 处理微信登录
function handleWechatLogin() {
    alert('微信登录功能需要后端支持，当前为演示模式');
}

// 检查登录状态
function checkAuth() {
    if (!isLoggedIn) {
        showLoginModal();
        return false;
    }
    return true;
}

// 加载技能列表
async function loadSkills() {
// 模拟技能数据
    allSkills = [
        { id: 'skill_1', name: '文本摘要', command: '/summarize', category: 'TEXT' },
        { id: 'skill_2', name: '英文翻译', command: '/translate_en', category: 'TEXT' },
        { id: 'skill_3', name: '中文翻译', command: '/translate_zh', category: 'TEXT' },
        { id: 'skill_4', name: '语法检查', command: '/grammar_check', category: 'TEXT' },
        { id: 'skill_5', name: '情感分析', command: '/sentiment_analysis', category: 'TEXT' },
        { id: 'skill_6', name: '关键词提取', command: '/extract_keywords', category: 'TEXT' },
        { id: 'skill_7', name: '文本分类', command: '/text_classification', category: 'TEXT' },
        { id: 'skill_8', name: '命名实体识别', command: '/ner', category: 'TEXT' },
        { id: 'skill_9', name: '文本纠错', command: '/text_correction', category: 'TEXT' },
        { id: 'skill_10', name: '文本生成', command: '/text_generation', category: 'TEXT' },
        { id: 'skill_11', name: '代码解释', command: '/explain_code', category: 'CODE' },
        { id: 'skill_12', name: '代码生成', command: '/generate_code', category: 'CODE' },
        { id: 'skill_13', name: '代码优化', command: '/optimize_code', category: 'CODE' },
        { id: 'skill_14', name: '代码调试', command: '/debug_code', category: 'CODE' },
        { id: 'skill_15', name: '问答系统', command: '/qa', category: 'ADVANCED' },
        { id: 'skill_16', name: '文本相似度', command: '/text_similarity', category: 'ADVANCED' },
        { id: 'skill_17', name: '文本聚类', command: '/text_clustering', category: 'ADVANCED' },
        { id: 'skill_18', name: '文本去重', command: '/deduplicate', category: 'ADVANCED' },
        { id: 'skill_19', name: '文本格式化', command: '/format_text', category: 'ADVANCED' },
        { id: 'skill_20', name: '文本加密', command: '/encrypt_text', category: 'ADVANCED' }
    ];

    renderSkills();
}

// 渲染技能列表
function renderSkills() {
    const textSkillsContainer = document.getElementById('textSkills');
    const codeSkillsContainer = document.getElementById('codeSkills');
    const advancedSkillsContainer = document.getElementById('advancedSkills');

// 清空容器
    textSkillsContainer.innerHTML = '';
    codeSkillsContainer.innerHTML = '';
    advancedSkillsContainer.innerHTML = '';

// 按类别分组
    allSkills.forEach(skill => {
        const skillItem = createSkillItem(skill);

        switch(skill.category) {
            case 'TEXT':
                textSkillsContainer.appendChild(skillItem);
                break;
            case 'CODE':
                codeSkillsContainer.appendChild(skillItem);
                break;
            case 'ADVANCED':
                advancedSkillsContainer.appendChild(skillItem);
                break;
            default:
                textSkillsContainer.appendChild(skillItem);
        }
    });

// 恢复当前选中的技能
    if (currentSkill) {
        const skillItem = document.querySelector(`.skill-item[data-skill-id="${currentSkill.id}"]`);
        if (skillItem) {
            skillItem.classList.add('active');

            const skillSelectorBtn = document.getElementById('skillSelectorBtn');
            const currentSkillIndicator = document.getElementById('currentSkillIndicator');

            skillSelectorBtn.classList.add('active');
            if (currentSkillIndicator) {
                currentSkillIndicator.innerHTML = `当前技能: <span>${currentSkill.name}</span>`;
            }

            const messageInput = document.getElementById('messageInput');
            messageInput.placeholder = `使用 ${currentSkill.name} 技能，请输入内容...`;
        }
    }
}

// 创建技能项
function createSkillItem(skill) {
    const skillItem = document.createElement('div');
    skillItem.className = 'skill-item';
    skillItem.textContent = skill.name;
    skillItem.dataset.skillId = skill.id;
    skillItem.dataset.command = skill.command;

    skillItem.addEventListener('click', () => {
        if (!checkAuth()) return;
        selectSkill(skill);
    });

    return skillItem;
}

// 选择技能
function selectSkill(skill) {
// 移除之前选中的技能
    if (currentSkill) {
        const previousSkillItem = document.querySelector(`.skill-item[data-skill-id="${currentSkill.id}"]`);
        if (previousSkillItem) {
            previousSkillItem.classList.remove('active');
        }
    }

// 设置当前技能
    currentSkill = skill;

// 更新技能选择器按钮状态
    const skillSelectorBtn = document.getElementById('skillSelectorBtn');
    const currentSkillIndicator = document.getElementById('currentSkillIndicator');

    skillSelectorBtn.classList.add('active');
    if (currentSkillIndicator) {
        currentSkillIndicator.innerHTML = `当前技能: <span>${skill.name}</span>`;
    }

// 更新技能项状态
    const skillItem = document.querySelector(`.skill-item[data-skill-id="${skill.id}"]`);
    if (skillItem) {
        skillItem.classList.add('active');
    }

// 更新输入框提示
    const messageInput = document.getElementById('messageInput');
    messageInput.placeholder = `使用 ${skill.name} 技能，请输入内容...`;

// 关闭技能选择菜单
    document.getElementById('skillSelectorMenu').classList.remove('show');

// 保存到本地存储
    saveToLocalStorage();

    console.log('已选择技能:', skill.name);
}

// 创建新聊天
async function createNewChat() {
    if (!checkAuth()) return null;

    try {
        const chatId = 'chat_' + Date.now();
        const newSession = {
            id: chatId,
            title: '新对话',
            updatedAt: new Date().toISOString(),
            messages: [],
            currentSkillId: null
        };

        chatSessions[chatId] = newSession;
        currentChatId = chatId;

        updateChatHistoryList();
        showEmptyState();

// 保存到本地存储
        saveToLocalStorage();

        return chatId;
    } catch (error) {
        console.error('创建新聊天失败:', error);
        return null;
    }
}

// 显示空状态
function showEmptyState() {
    const messagesContainer = document.getElementById('messagesContainer');
    messagesContainer.innerHTML = `
<div class="empty-state" id="emptyState">
    <i class="fas fa-comments"></i>
    <p>开始与豆包对话吧！</p>
    <div class="hint">选择一个技能，然后输入您的内容</div>
</div>
`;
}

// 更新聊天历史列表
function updateChatHistoryList() {
    const chatHistory = document.getElementById('chatHistory');
    chatHistory.innerHTML = '';

// 按更新时间倒序排序
    const sortedChats = Object.values(chatSessions).sort((a, b) => {
        return new Date(b.updatedAt) - new Date(a.updatedAt);
    });

    sortedChats.forEach(chat => {
        const historyItem = document.createElement('div');
        historyItem.className = `history-item ${chat.id === currentChatId ? 'active' : ''}`;
        historyItem.dataset.chatId = chat.id;

// 获取最后一条消息作为预览
        const lastMessage = chat.messages && chat.messages.length > 0 ?
            chat.messages[chat.messages.length - 1] : null;
        const previewText = lastMessage ?
            (lastMessage.content && lastMessage.content.length > 20 ?
                lastMessage.content.substring(0, 20) + '...' : lastMessage.content) :
            '新对话';

        historyItem.innerHTML = `
<div class="history-icon">
    <i class="fas fa-comments"></i>
</div>
<div class="history-info">
    <div class="history-title">${chat.title}</div>
    <div class="history-preview">${previewText}</div>
</div>
<button class="delete-chat-btn" title="删除聊天">
    <i class="fas fa-times"></i>
</button>
`;

// 点击聊天项切换聊天
        historyItem.addEventListener('click', (e) => {
            if (!e.target.closest('.delete-chat-btn')) {
                switchToChat(chat.id);
            }
        });

// 删除聊天按钮
        const deleteBtn = historyItem.querySelector('.delete-chat-btn');
        deleteBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            deleteChat(chat.id);
        });

        chatHistory.appendChild(historyItem);
    });
}

// 切换到指定聊天
async function switchToChat(chatId) {
    if (!chatSessions[chatId]) {
        console.error('聊天会话不存在:', chatId);
        return;
    }

    const chat = chatSessions[chatId];
    currentChatId = chat.id;

// 更新聊天标题
    document.querySelector('.chat-name').textContent = chat.title;

// 清空消息容器
    const messagesContainer = document.getElementById('messagesContainer');
    messagesContainer.innerHTML = '';

// 添加消息到界面
    if (chat.messages && chat.messages.length > 0) {
        chat.messages.forEach(message => {
            addMessageToUI(message);
        });
    } else {
// 显示空状态
        showEmptyState();
    }

// 恢复当前会话的技能状态
    if (chat.currentSkillId) {
        const skill = allSkills.find(s => s.id === chat.currentSkillId);
        if (skill) {
            selectSkill(skill);
        }
    }

// 更新聊天历史列表的激活状态
    updateChatHistoryList();

// 保存到本地存储
    saveToLocalStorage();

    console.log('切换到聊天:', chatId);
}

// 删除聊天
async function deleteChat(chatId) {
    if (Object.keys(chatSessions).length <= 1) {
        alert('至少需要保留一个聊天会话');
        return;
    }

    try {
        delete chatSessions[chatId];

// 如果删除的是当前聊天，切换到另一个聊天
        if (chatId === currentChatId) {
            const remainingChatIds = Object.keys(chatSessions);
            if (remainingChatIds.length > 0) {
                await switchToChat(remainingChatIds[0]);
            } else {
                await createNewChat();
            }
        }

        updateChatHistoryList();

// 保存到本地存储
        saveToLocalStorage();

        console.log('删除聊天成功:', chatId);
    } catch (error) {
        console.error('删除聊天失败:', error);
        alert('删除聊天失败: ' + error.message);
    }
}

// 发送消息
async function sendMessage() {
    if (!checkAuth()) return;

    if (isProcessing) {
        return; // 防止重复发送
    }

    const messageInput = document.getElementById('messageInput');
    const text = messageInput.value.trim();
    const sendButton = document.getElementById('sendButton');

// 检查是否已选择技能
    if (!currentSkill) {
        alert('请先选择一个技能！');
        return;
    }

    if (text === '' && pendingImages.length === 0) {
        return;
    }

    isProcessing = true;
    sendButton.disabled = true;

    try {
// 显示处理中指示器
        showProcessingIndicator();

// 创建用户消息对象
        const userMessage = {
            id: 'user_msg_' + Date.now(),
            type: 'USER',
            content: text,
            timestamp: new Date().toISOString(),
            imageUrls: pendingImages.map(img => img.data)
        };

// 添加用户消息到UI
        addMessageToUI(userMessage);

// 模拟AI回复
        setTimeout(() => {
// 隐藏处理中指示器
            hideProcessingIndicator();

// 生成AI回复
            const aiReply = generateAIResponse(text, pendingImages.length > 0, currentSkill);

// 创建AI消息对象
            const aiMessage = {
                id: 'ai_msg_' + Date.now(),
                type: 'AI',
                content: aiReply,
                timestamp: new Date().toISOString()
            };

// 添加AI回复到UI
            addMessageToUI(aiMessage);

// 更新聊天会话
            if (chatSessions[currentChatId]) {
                if (!chatSessions[currentChatId].messages) {
                    chatSessions[currentChatId].messages = [];
                }

                chatSessions[currentChatId].messages.push(userMessage);
                chatSessions[currentChatId].messages.push(aiMessage);
                chatSessions[currentChatId].updatedAt = new Date().toISOString();
                chatSessions[currentChatId].currentSkillId = currentSkill.id;
                chatSessions[currentChatId].title = text.substring(0, 20) + (text.length > 20 ? '...' : '');
            }

// 清空输入框和图片
            messageInput.value = '';
            clearPendingImages();

// 更新聊天历史列表
            updateChatHistoryList();

// 保存到本地存储
            saveToLocalStorage();

            isProcessing = false;
            sendButton.disabled = false;
        }, 1500); // 模拟处理延迟

    } catch (error) {
        console.error('发送消息失败:', error);
        hideProcessingIndicator();
        alert('发送消息失败: ' + error.message);
        isProcessing = false;
        sendButton.disabled = false;
    }
}

// 生成AI回复
function generateAIResponse(text, hasImages, skill) {
    const responses = {
        '文本摘要': `我已经为您总结了文本内容：${text.substring(0, 50)}...`,
        '英文翻译': `英文翻译结果：This is the translation of "${text}"`,
        '中文翻译': `中文翻译结果：这是"${text}"的翻译`,
        '语法检查': `语法检查完成，发现3处可以改进的地方。`,
        '情感分析': `情感分析结果：这段文字表达了积极的情感。`,
        '关键词提取': `提取的关键词：AI, 智能, 技术, 未来`,
        '文本分类': `文本分类结果：这段文字属于科技类内容。`,
        '命名实体识别': `识别到的实体：北京（地点）、2023年（时间）`,
        '文本纠错': `文本纠错完成，已修正2处错误。`,
        '文本生成': `根据您的要求生成的文本：人工智能正在改变我们的生活方式...`,
        '代码解释': `这段代码的功能是...`,
        '代码生成': `生成的代码示例：function example() { return "Hello World"; }`,
        '代码优化': `代码优化建议：可以使用更高效的算法来提升性能。`,
        '代码调试': `发现的潜在问题：第5行可能存在空指针异常。`,
        '问答系统': `根据您的问题，我的回答是：人工智能是模拟人类智能的技术。`,
        '文本相似度': `文本相似度分析结果：这两段文本的相似度为75%。`,
        '文本聚类': `文本聚类完成，已将内容分为3个类别。`,
        '文本去重': `文本去重完成，已移除5处重复内容。`,
        '文本格式化': `文本格式化完成，已按照标准格式调整。`,
        '文本加密': `文本加密完成，加密结果：a1b2c3d4e5f6`
    };

    return responses[skill.name] || `我已经使用${skill.name}技能处理了您的请求。${hasImages ? '并且分析了您上传的图片。' : ''}`;
}

// 显示处理中指示器
function showProcessingIndicator() {
    const messagesContainer = document.getElementById('messagesContainer');
    const emptyState = document.getElementById('emptyState');

    if (emptyState) {
        emptyState.remove();
    }

    const processingDiv = document.createElement('div');
    processingDiv.className = 'message received';
    processingDiv.id = 'processingIndicator';

    const avatar = document.createElement('div');
    avatar.className = 'message-avatar';
    avatar.innerHTML = '<i class="fas fa-robot"></i>';

    const content = document.createElement('div');
    content.className = 'message-content';

    const processingText = document.createElement('div');
    processingText.className = 'message-text';
    processingText.innerHTML = `
<div>正在使用 ${currentSkill.name} 技能处理您的请求...</div>
<div class="processing-indicator">
    <div class="processing-dots">
        <div class="processing-dot"></div>
        <div class="processing-dot"></div>
        <div class="processing-dot"></div>
    </div>
</div>
`;

    content.appendChild(processingText);
    processingDiv.appendChild(avatar);
    processingDiv.appendChild(content);
    messagesContainer.appendChild(processingDiv);

    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 隐藏处理中指示器
function hideProcessingIndicator() {
    const processingIndicator = document.getElementById('processingIndicator');
    if (processingIndicator) {
        processingIndicator.remove();
    }
}

// 添加消息到UI
function addMessageToUI(message) {
    const messagesContainer = document.getElementById('messagesContainer');
    const emptyState = document.getElementById('emptyState');

    if (emptyState) {
        emptyState.remove();
    }

    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${message.type === 'USER' ? 'sent' : 'received'}`;
    messageDiv.dataset.messageId = message.id;

// 消息头像
    const messageAvatar = document.createElement('div');
    messageAvatar.className = 'message-avatar';
    messageAvatar.innerHTML = message.type === 'USER' ?
        '<i class="fas fa-user"></i>' : '<i class="fas fa-robot"></i>';

// 消息内容
    const messageContent = document.createElement('div');
    messageContent.className = 'message-content';

// 文本内容
    if (message.content) {
        const messageText = document.createElement('div');
        messageText.className = 'message-text';
        messageText.textContent = message.content;
        messageContent.appendChild(messageText);
    }

// 图片内容
    if (message.imageUrls && message.imageUrls.length > 0) {
        const messageImages = document.createElement('div');
        messageImages.className = 'message-images';

        message.imageUrls.forEach(imageData => {
            const img = document.createElement('img');
            img.src = imageData;
            img.alt = '消息图片';
            img.className = 'message-image';
            img.onclick = () => previewImage(imageData);
            messageImages.appendChild(img);
        });

        messageContent.appendChild(messageImages);
    }

// 消息时间
    const messageTime = document.createElement('div');
    messageTime.className = 'message-time';
    const timestamp = new Date(message.timestamp);
    messageTime.textContent = timestamp.toLocaleTimeString('zh-CN', {
        hour: '2-digit', minute: '2-digit'
    });
    messageContent.appendChild(messageTime);

    if (message.type === 'USER') {
        messageDiv.appendChild(messageContent);
        messageDiv.appendChild(messageAvatar);
    } else {
        messageDiv.appendChild(messageAvatar);
        messageDiv.appendChild(messageContent);
    }

    messagesContainer.appendChild(messageDiv);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 重置当前技能
function resetCurrentSkill() {
    currentSkill = null;

// 更新技能选择器按钮状态
    const skillSelectorBtn = document.getElementById('skillSelectorBtn');
    const currentSkillIndicator = document.getElementById('currentSkillIndicator');

    skillSelectorBtn.classList.remove('active');
    if (currentSkillIndicator) {
        currentSkillIndicator.innerHTML = '';
    }

// 移除所有技能项的激活状态
    document.querySelectorAll('.skill-item').forEach(item => {
        item.classList.remove('active');
    });

// 更新输入框提示
    const messageInput = document.getElementById('messageInput');
    messageInput.placeholder = '请先选择一个技能，然后输入内容';

// 保存到本地存储
    saveToLocalStorage();
}

// 显示VIP积分模态框
function showVipModal() {
    document.getElementById('vipModal').classList.add('active');
}

// 隐藏VIP积分模态框
function hideVipModal() {
    document.getElementById('vipModal').classList.remove('active');
}

// 处理退出登录
function handleLogout() {
    if (confirm('确定要退出登录吗？')) {
// 清除登录状态
        isLoggedIn = false;
        currentUser = null;
        localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);

// 重置界面状态
        updateUserInterface();
        resetCurrentSkill();

// 隐藏用户下拉菜单
        document.getElementById('userDropdown').classList.remove('active');

        alert('已成功退出登录');
    }
}

// 处理图片选择
function handleImageSelect(e) {
    const files = e.target.files;
    if (files.length > 0) {
        handleFileSelect(files);
    }
}

// 处理文件选择
function handleFileSelect(files) {
    for (let file of files) {
// 检查文件类型
        if (!file.type.startsWith('image/')) {
            alert('请选择图片文件！');
            continue;
        }

// 检查文件大小（限制为2MB）
        if (file.size > 2 * 1024 * 1024) {
            alert(`图片 ${file.name} 太大，请选择小于2MB的图片`);
            continue;
        }

// 检查总图片数量限制
        if (pendingImages.length >= 5) {
            alert('最多只能上传5张图片');
            break;
        }

        addPendingImage(file);
    }
}

// 添加图片到待发送列表
function addPendingImage(file) {
    const reader = new FileReader();

    reader.onload = function(e) {
        const imageData = {
            id: Date.now() + Math.random(),
            name: file.name,
            type: file.type,
            size: file.size,
            data: e.target.result
        };

        pendingImages.push(imageData);
        updateImageThumbnails();
    };

    reader.onerror = function(error) {
        console.error('读取图片文件失败:', error);
        alert('读取图片文件失败，请重试');
    };

    reader.readAsDataURL(file);
}

// 更新图片缩略图显示
function updateImageThumbnails() {
    const inputActions = document.getElementById('inputActions');
    const uploadButton = document.getElementById('uploadImageButton');

// 清空除上传按钮外的所有内容
    inputActions.innerHTML = '';
    if (uploadButton) {
        inputActions.appendChild(uploadButton);
    }

// 为每张图片创建一个缩略图
    pendingImages.forEach((image, index) => {
        const thumbnail = createImageThumbnail(image, index);
        inputActions.appendChild(thumbnail);
    });
}

// 创建图片缩略图
function createImageThumbnail(image, index) {
    const thumbnail = document.createElement('div');
    thumbnail.className = 'image-thumbnail';
    thumbnail.title = image.name;
    thumbnail.dataset.imageId = image.id;

    const img = document.createElement('img');
    img.src = image.data;
    img.alt = image.name;

    const deleteBtn = document.createElement('div');
    deleteBtn.className = 'delete-btn';
    deleteBtn.innerHTML = '×';
    deleteBtn.onclick = (e) => {
        e.stopPropagation();
        removePendingImage(image.id);
    };

    thumbnail.appendChild(img);
    thumbnail.appendChild(deleteBtn);

// 点击缩略图预览图片
    thumbnail.onclick = () => previewImage(image.data);

    return thumbnail;
}

// 预览图片
function previewImage(imageSrc) {
    const modal = document.getElementById('imagePreviewModal');
    const previewImage = document.getElementById('previewImage');

    if (modal && previewImage) {
        previewImage.src = imageSrc;
        modal.classList.add('active');

// 点击模态框背景关闭预览
        modal.onclick = function(e) {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        };
    }
}

// 移除待发送图片
function removePendingImage(imageId) {
    pendingImages = pendingImages.filter(img => img.id !== imageId);
    updateImageThumbnails();
}

// 清空所有待发送图片
function clearPendingImages() {
    pendingImages = [];
    updateImageThumbnails();
}

// 绑定事件
function bindEvents() {
// 关闭登录按钮
    document.getElementById('closeLoginBtn').addEventListener('click', hideLoginModal);

// 用户头像点击事件
    document.getElementById('userProfile').addEventListener('click', (e) => {
        if (!isLoggedIn) {
            showLoginModal();
        } else {
// 切换下拉菜单显示/隐藏
            const dropdown = document.getElementById('userDropdown');
            dropdown.classList.toggle('active');
        }
    });

// 点击页面其他区域关闭下拉菜单
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.user-profile')) {
            document.getElementById('userDropdown').classList.remove('active');
        }
    });

// VIP积分按钮点击事件
    document.getElementById('vipOption').addEventListener('click', () => {
        document.getElementById('userDropdown').classList.remove('active');
        showVipModal();
    });

// 清理聊天记录按钮
    document.getElementById('cleanupOption').addEventListener('click', () => {
        document.getElementById('userDropdown').classList.remove('active');
        if (confirm('确定要清理所有聊天记录吗？此操作不可恢复！')) {
            clearAllChatSessions();
        }
    });

// 退出登录按钮点击事件
    document.getElementById('logoutOption').addEventListener('click', () => {
        document.getElementById('userDropdown').classList.remove('active');
        handleLogout();
    });

// 关闭VIP模态框按钮
    document.getElementById('closeVipBtn').addEventListener('click', hideVipModal);

// 登录相关事件
    document.getElementById('loginBtn').addEventListener('click', handleLogin);
    document.getElementById('registerBtn').addEventListener('click', handleRegister);
    document.getElementById('qqLoginBtn').addEventListener('click', handleQQLogin);
    document.getElementById('wechatLoginBtn').addEventListener('click', handleWechatLogin);

// 登录/注册切换
    document.getElementById('goToRegister').addEventListener('click', (e) => {
        e.preventDefault();
// 切换到注册标签
        document.querySelectorAll('.login-tab').forEach(tab => {
            tab.classList.remove('active');
            if (tab.dataset.tab === 'register') {
                tab.classList.add('active');
            }
        });
        document.querySelectorAll('.login-form').forEach(form => {
            form.classList.remove('active');
            if (form.id === 'registerForm') {
                form.classList.add('active');
            }
        });
    });

    document.getElementById('goToLogin').addEventListener('click', (e) => {
        e.preventDefault();
// 切换到登录标签
        document.querySelectorAll('.login-tab').forEach(tab => {
            tab.classList.remove('active');
            if (tab.dataset.tab === 'login') {
                tab.classList.add('active');
            }
        });
        document.querySelectorAll('.login-form').forEach(form => {
            form.classList.remove('active');
            if (form.id === 'loginForm') {
                form.classList.add('active');
            }
        });
    });

// 登录标签切换
    document.querySelectorAll('.login-tab').forEach(tab => {
        tab.addEventListener('click', function() {
            const tabName = this.getAttribute('data-tab');

// 更新标签状态
            document.querySelectorAll('.login-tab').forEach(t => t.classList.remove('active'));
            this.classList.add('active');

// 更新表单显示
            document.querySelectorAll('.login-form').forEach(form => form.classList.remove('active'));
            document.getElementById(`${tabName}Form`).classList.add('active');
        });
    });

// 新建聊天按钮
    document.getElementById('newChatBtn').addEventListener('click', createNewChat);

// 发送按钮
    document.getElementById('sendButton').addEventListener('click', sendMessage);

// 上传图片按钮
    document.getElementById('uploadImageButton').addEventListener('click', () => {
        if (!checkAuth()) return;
        document.getElementById('imageInput').click();
    });

// 消息输入框回车发送
    document.getElementById('messageInput').addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

// 图片选择
    document.getElementById('imageInput').addEventListener('change', handleImageSelect);

// 技能选择器
    const skillSelectorBtn = document.getElementById('skillSelectorBtn');
    const skillSelectorMenu = document.getElementById('skillSelectorMenu');

    skillSelectorBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        if (!checkAuth()) return;
        skillSelectorMenu.classList.toggle('show');
    });

// 点击页面其他区域关闭菜单
    document.addEventListener('click', () => {
        skillSelectorMenu.classList.remove('show');
    });


// 阻止菜单内部点击事件冒泡
    skillSelectorMenu.addEventListener('click', (e) => {
        e.stopPropagation();
    });

// 图片预览模态框点击关闭
    const imagePreviewModal = document.getElementById('imagePreviewModal');
    if (imagePreviewModal) {
        imagePreviewModal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('active');
            }
        });
    }

// VIP模态框点击关闭
    const vipModal = document.getElementById('vipModal');
    if (vipModal) {
        vipModal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('active');
            }
        });
    }

// VIP功能按钮
    document.getElementById('rechargeBtn').addEventListener('click', function() {
        alert('充值功能需要后端支持，当前为演示模式');
    });

    document.getElementById('vipHistoryBtn').addEventListener('click', function() {
        alert('积分记录功能需要后端支持，当前为演示模式');
    });

// 初始化拖拽功能
    initDragAndDrop();
}

// 初始化拖拽功能
function initDragAndDrop() {
    const chatApp = document.querySelector('.chat-app');
    const messagesContainer = document.getElementById('messagesContainer');

// 阻止默认拖拽行为
    const preventDefault = (e) => {
        e.preventDefault();
        e.stopPropagation();
    };

// 拖拽进入
    const handleDragEnter = (e) => {
        preventDefault(e);
    };

// 拖拽离开
    const handleDragLeave = (e) => {
        preventDefault(e);
    };

// 放置文件
    const handleDrop = (e) => {
        preventDefault(e);

        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files);
        }
    };

// 为整个聊天应用添加拖拽事件
    chatApp.addEventListener('dragenter', handleDragEnter);
    chatApp.addEventListener('dragover', preventDefault);
    chatApp.addEventListener('dragleave', handleDragLeave);
    chatApp.addEventListener('drop', handleDrop);

// 为消息容器单独添加拖拽事件
    messagesContainer.addEventListener('dragenter', handleDragEnter);
    messagesContainer.addEventListener('dragover', preventDefault);
    messagesContainer.addEventListener('dragleave', handleDragLeave);
    messagesContainer.addEventListener('drop', handleDrop);
}

// 页面关闭前保存数据
window.addEventListener('beforeunload', function() {
    saveToLocalStorage();
});

// 定期自动保存（每30秒）
setInterval(() => {
    if (Object.keys(chatSessions).length > 0) {
        saveToLocalStorage();
    }
}, 30000);

// 初始化技能选择器菜单显示/隐藏
document.addEventListener('click', function(e) {
    const skillSelectorMenu = document.getElementById('skillSelectorMenu');
    const skillSelectorBtn = document.getElementById('skillSelectorBtn');

    if (!skillSelectorBtn.contains(e.target) && !skillSelectorMenu.contains(e.target)) {
        skillSelectorMenu.classList.remove('show');
    }
});

// 处理键盘快捷键
document.addEventListener('keydown', function(e) {
// Ctrl+Enter 发送消息
    if (e.ctrlKey && e.key === 'Enter') {
        e.preventDefault();
        sendMessage();
    }

// Escape 键关闭所有模态框
    if (e.key === 'Escape') {
        hideLoginModal();
        hideVipModal();
        document.getElementById('imagePreviewModal').classList.remove('active');
        document.getElementById('userDropdown').classList.remove('active');
        document.getElementById('skillSelectorMenu').classList.remove('show');
    }
});

// 添加响应式设计支持
function handleResize() {
    const chatApp = document.querySelector('.chat-app');
    const sidebar = document.querySelector('.sidebar');

    if (window.innerWidth < 768) {
// 移动端样式调整
        chatApp.style.flexDirection = 'column';
        sidebar.style.width = '100%';
        sidebar.style.height = '200px';
    } else {
// 桌面端样式
        chatApp.style.flexDirection = 'row';
        sidebar.style.width = '280px';
        sidebar.style.height = 'auto';
    }
}

// 监听窗口大小变化
window.addEventListener('resize', handleResize);

// 初始化响应式设计
handleResize();

// 导出功能（用于调试）
window.chatApp = {
    chatSessions,
    currentChatId,
    currentSkill,
    isLoggedIn,
    currentUser,
    saveToLocalStorage,
    clearAllChatSessions,
    createNewChat,
    switchToChat,
    sendMessage
};

console.log('图片智能助手应用初始化完成！');