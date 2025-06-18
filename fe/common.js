const baseUrl = 'http://localhost:8080';
const chunkSize = 200 * 1024; // Set the desired chunk size (200KB in this example)

let uploadedFiles = [];

function handleFileSelect(event, fileInputId) {
    const file = event.target.files[0];
    if (!file) return;

    const fileInfo = document.getElementById(`${fileInputId}Info`);
    const uploadBtn = document.getElementById(`${fileInputId}UploadBtn`);

    // Display file information
    fileInfo.innerHTML = `
        <div style="display: flex; align-items: center; margin-bottom: 10px;">
            <div style="font-size: 24px; margin-right: 10px;">${getFileIcon(file.type)}</div>
            <div>
                <div style="font-weight: 600; color: #333;">${file.name}</div>
                <div style="color: #666; font-size: 14px;">
                    ${formatFileSize(file.size)} • ${file.type || 'Unknown type'}
                </div>
            </div>
        </div>
    `;

    fileInfo.classList.add('show');

    // Enable upload button when both file and document type are selected
    checkUploadReady(fileInputId);
}

function checkUploadReady(fileInputId) {
    const fileInput = document.getElementById(fileInputId);
    const uploadBtn = document.getElementById(`${fileInputId}UploadBtn`);

    const hasFile = fileInput.files.length > 0;

    uploadBtn.disabled = !(hasFile);
}

function setupDragAndDrop(inputId) {
    const label = document.querySelector(`label[for="${inputId}"]`);

    label.addEventListener('dragover', (e) => {
        e.preventDefault();
        label.style.borderColor = '#667eea';
        label.style.background = '#f5f7ff';
    });

    label.addEventListener('dragleave', (e) => {
        e.preventDefault();
        label.style.borderColor = '#ccc';
        label.style.background = '#fafafa';
    });

    label.addEventListener('drop', (e) => {
        e.preventDefault();
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            document.getElementById(inputId).files = files;
            handleFileSelect({ target: { files: files } });
        }
        label.style.borderColor = '#ccc';
        label.style.background = '#fafafa';
    });
}

function displayUploadedFiles() {
    const uploadedFilesDiv = document.getElementById('uploadedFiles');
    const fileList = document.getElementById('fileList');

    if (uploadedFiles.length === 0) {
        uploadedFilesDiv.style.display = 'none';
        return;
    }

    uploadedFilesDiv.style.display = 'block';
    fileList.innerHTML = '';

    uploadedFiles.forEach(file => {
        const fileItem = document.createElement('div');
        fileItem.className = 'file-item';
        fileItem.innerHTML = `
            <div class="file-details">
                <div class="file-type-icon ${file.type}">🔄</div>
                <div>
                    <div style="font-weight: 600; color: #333;">${file.name}</div>
                    <div style="color: #666; font-size: 14px;">
                        ${file.documentType} • ${formatFileSize(file.size)} • ${formatDate(file.uploadDate)}
                    </div>
                </div>
            </div>
            <button class="download-btn" onclick="downloadFile('${file.fileKey}', '${file.name}')">
                Download
            </button>
        `;
        fileList.appendChild(fileItem);
    });
}

function setProgress(progressBarId, percentage) {
    const progressBar = document.getElementById(progressBarId);
    const progressFill = progressBar.querySelector('.progress-fill');

    progressBar.classList.add('show');
    progressFill.style.width = `${percentage}%`;

    if (percentage === 100) {
        setTimeout(() => {
            progressBar.classList.remove('show');
            progressFill.style.width = '0%';
        }, 2000);
    }
}

function showStatus(statusDivId, message, statusType) {
    const statusDiv = document.getElementById(statusDivId);
    statusDiv.textContent = message;
    statusDiv.className = `status-message show ${statusType}`;

    if (statusType === 'success') {
        setTimeout(() => {
            statusDiv.classList.remove('show');
        }, 5000);
    }
}

// Utility functions
function getFileIcon(mimeType) {
    if (mimeType.startsWith('image/')) return '🖼️';
    if (mimeType === 'application/pdf') return '📄';
    return '📁';
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}