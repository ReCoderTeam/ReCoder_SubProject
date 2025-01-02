
    function updateFileList() {
    const fileInput = document.getElementById('inquiryFiles');
    const fileListContainer = document.getElementById('fileListContainer');
    fileListContainer.innerHTML = ''; // Clear previous file list

    for (let i = 0; i < fileInput.files.length; i++) {
    const file = fileInput.files[i];
    const fileLink = document.createElement('div');
    fileLink.className = 'attachmentFile';
    fileLink.textContent = file.name; // Display file name

    // Create a delete button
    const removeButton = document.createElement('button');
    removeButton.textContent = '삭제';
    removeButton.onclick = function() {
    removeFile(file);
};

    fileLink.appendChild(removeButton);
    fileListContainer.appendChild(fileLink);
}
}

    function removeFile(file) {
    const fileInput = document.getElementById('inquiryFiles');
    const fileList = Array.from(fileInput.files);
    const index = fileList.indexOf(file);
    if (index > -1) {
    fileList.splice(index, 1);
    const dataTransfer = new DataTransfer();
    fileList.forEach(f => dataTransfer.items.add(f));
    fileInput.files = dataTransfer.files;
    updateFileList(); // Update the file list
}
}
