// === Custom Bootstrap Alert Modal ===
function customAlert(message, title = "Notification", type = "info") {
    const modal = new bootstrap.Modal(document.getElementById('customAlertModal'));
    const titleElem = document.getElementById('customAlertLabel');
    const msgElem = document.getElementById('customAlertMessage');
    const modalHeader = document.querySelector('#customAlertModal .modal-header');

    // Set title & message
    titleElem.textContent = title;
    msgElem.innerHTML = message;

    // Reset header colors
    modalHeader.className = "modal-header text-white";
    switch (type) {
        case "success":
            modalHeader.classList.add("bg-success");
            break;
        case "error":
            modalHeader.classList.add("bg-danger");
            break;
        case "warning":
            modalHeader.classList.add("bg-warning", "text-dark");
            break;
        default:
            modalHeader.classList.add("bg-primary");
    }

    modal.show();
}

// === Custom Confirm Modal (returns Promise) ===
function customConfirm(message) {
    return new Promise((resolve) => {
        const modalEl = document.getElementById('customConfirmModal');
        const modal = new bootstrap.Modal(modalEl);
        const msgElem = document.getElementById('customConfirmMessage');
        const yesBtn = document.getElementById('confirmYesBtn');

        msgElem.textContent = message;

        const handler = () => {
            yesBtn.removeEventListener('click', handler);
            modal.hide();
            resolve(true);
        };

        yesBtn.addEventListener('click', handler);
        modalEl.addEventListener('hidden.bs.modal', () => resolve(false), { once: true });
        modal.show();
    });
}


async function changeState(token, action) {
    try {
        const res = await fetch(`/api/requests/${token}/${action}`, { method: 'PUT' });
        if (res.ok) customAlert('success', `Request ${action}ed successfully`);
        else customAlert('danger', `Failed to ${action} request`);
    } catch {
        customAlert('danger', 'Network error');
    }
    await loadRequests();
}

// === Action button functionality ===
function actionButtons(r) {
    const t = r.token;
    const s = r.requestStatus?.toLowerCase();

    const buttons = [];

    if (s === 'received') {
        buttons.push(`<button class="btn btn-secondary btn-sm" onclick="changeState('${t}','assign')">Assign</button>`);
    }
    else if (s === 'assigned') {
        buttons.push(`<button class="btn btn-primary btn-sm" onclick="changeState('${t}','start')">Start</button>`);
    }
    else if (s === 'in_progress') {
        buttons.push(`<button class="btn btn-success btn-sm" onclick="changeState('${t}','complete')">Complete</button>`);
    }

    if (buttons.length === 0)
        return `<span class="text-muted small">No actions available</span>`;

    return `<div class="btn-group" role="group">${buttons.join('')}</div>`;
}