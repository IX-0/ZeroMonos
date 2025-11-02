// === Load Residues and Update Table + Select ===
async function loadResidues(query = "") {
    const url = query
        ? `/api/residues/search/${encodeURIComponent(query)}`
        : `/api/residues`;

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error("Failed to fetch residues");

        const residues = await response.json();

        // Update table
        const tableBody = document.getElementById("residues-table-body");
        if (tableBody) {
            tableBody.innerHTML = "";

            if (residues.length === 0) {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-muted">No residues found.</td>
                    </tr>`;
            } else {
                residues.forEach(r => {
                    const row = `
                        <tr>
                            <td>${r.id ?? ""}</td>
                            <td>${r.name ?? ""}</td>
                            <td>${r.desc ?? ""}</td>
                            <td>${r.weight ?? ""}</td>
                            <td>${r.volume ?? ""}</td>
                            <td>${r.requestToken ?? "Not linked"}</td>
                            <td>
                                <button class="btn btn-sm btn-danger" onclick="deleteResidue(${r.id})">Delete</button>
                            </td>
                        </tr>`;
                    tableBody.insertAdjacentHTML("beforeend", row);
                });
            }
        }

        // Update residue selection options (for Request form)
        const residueSelect = document.getElementById("residue-select");
        if (residueSelect) {
            residueSelect.innerHTML = ""; // Clear old options
            const availableResidues = residues.filter(r => !r.requestToken);

            if (availableResidues.length === 0) {
                const option = document.createElement("option");
                option.disabled = true;
                option.textContent = "No available residues";
                residueSelect.appendChild(option);
            } else {
                availableResidues.forEach(r => {
                    const option = document.createElement("option");
                    option.value = r.id;
                    option.textContent = r.name || `Residue #${r.id}`;
                    residueSelect.appendChild(option);
                });
            }
        }
    } catch (error) {
        console.error("Error loading residues:", error);
        customAlert("Error loading residues", "Error", "error");

        const tableBody = document.getElementById("residues-table-body");
        if (tableBody) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-danger">Error loading residues</td>
                </tr>`;
        }
    }
}

// === Search Residues ===
async function searchResidues() {
    const query = document.getElementById("search-input").value;
    await loadResidues(query);
}

// === Create Residue ===
async function createResidue(event) {
    event.preventDefault();

    const form = event.target;
    const data = {
        name: form.name.value,
        desc: form.desc.value,
        weight: parseFloat(form.weight.value),
        volume: parseFloat(form.volume.value)
    };

    const response = await fetch("/api/residues", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    if (response.ok) {
        form.reset();
        await loadResidues();
        customAlert("Residue created successfully!", "Success", "success");
    } else {
        customAlert("Failed to create residue", "Error", "error");
    }
}

// === Delete Residue ===
async function deleteResidue(id) {
    if (!(await customConfirm("Are you sure you want to delete this residue?"))) return;

    const response = await fetch(`/api/residues/${id}`, { method: "DELETE" });
    if (response.ok) {
        await loadResidues();
        customAlert("Residue deleted successfully!", "Deleted", "success");
    } else {
        customAlert("Failed to delete residue", "Error", "error");
    }
}

function getRequestActions(req) {
    const s = req.requestStatus?.toLowerCase();
    const t = req.token;

    let buttons = [];

    // === Rules ===
    // received, assigned → cancel or delete
    if (s === "received" || s === "assigned") {
        buttons.push(`<button class="btn btn-sm btn-warning me-1" onclick="changeState('${t}','cancel')">Cancel</button>`);
        buttons.push(`<button class="btn btn-sm btn-danger" onclick="deleteRequest('${t}')">Delete</button>`);
    }
    // in_progress → no cancel, no delete
    else if (s === "in_progress") {
        buttons.push(`<span class="text-muted small">No actions</span>`);
    }
    // completed, canceled → delete only
    else if (s === "completed" || s === "canceled") {
        buttons.push(`<button class="btn btn-sm btn-danger" onclick="deleteRequest('${t}')">Delete</button>`);
    }

    if (buttons.length === 0)
        return `<span class="text-muted small">No actions</span>`;

    return buttons.join(" ");
}


// === Create Request ===
async function createRequest(event) {
    event.preventDefault();

    const form = event.target;
    const residueSelect = document.getElementById('residue-select');
    const municipalitySelect = document.getElementById('municipality-select');

    const selectedResidues = Array.from(residueSelect.selectedOptions).map(opt => ({
        id: parseInt(opt.value)
    }));

    const data = {
        municipality: municipalitySelect.value,
        datetime: form.datetime.value,
        residues: selectedResidues
    };

    try {
        const response = await fetch('/api/requests', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const token = await response.text();
            await customAlert(`Request created successfully!<br><strong>Token:</strong> ${token}`, "Success", "success");
            await loadResidues();
            await loadRequests();
            form.reset();
        } else {
            const error = await response.text();
            customAlert(`Failed to create request: ${error || response.statusText}`, "Error", "error");
        }
    } catch (error) {
        console.error('Error creating request:', error);
        customAlert(`Network error: ${error.message}`, "Error", "error");
    }
}

// === Load Municipalities ===
async function loadMunicipalities() {
    const select = document.getElementById('municipality-select');
    select.innerHTML = '<option>Loading...</option>';

    try {
        const response = await fetch('https://json.geoapi.pt/municipios');
        if (!response.ok) throw new Error('Failed to fetch municipalities');

        const data = await response.json();
        select.innerHTML = '<option value="">Select municipality</option>';

        Object.values(data).flat().forEach(municipality => {
            const option = document.createElement('option');
            option.value = municipality;
            option.textContent = municipality;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error fetching municipalities:', error);
        select.innerHTML = '<option value="">Error loading municipalities</option>';
        customAlert("Failed to load municipalities", "Error", "error");
    }
}

// === Load Requests ===
async function loadRequests() {
    const tbody = document.getElementById('requests-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Loading...</td></tr>';

    try {
        const response = await fetch('/api/requests');
        if (!response.ok) throw new Error('Failed to fetch requests');
        const requests = await response.json();

        if (requests.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No requests found.</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        requests.forEach(req => {
            const residuesList = (req.residues || []).map(r => r.name || `#${r.id}`).join(', ') || '<em>None</em>';
            const row = document.createElement('tr');

            row.innerHTML = `
                <td><span class="text-monospace">${req.token}</span></td>
                <td>${req.municipality}</td>
                <td>${new Date(req.datetime).toLocaleString()}</td>
                <td><span class="badge bg-info text-dark">${req.requestStatus}</span></td>
                <td>${residuesList}</td>
                <td class="text-end">
                    ${getRequestActions(req)}
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading requests:', error);
        tbody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error loading requests</td></tr>`;
        customAlert("Error loading requests", "Error", "error");
    }
}


// === Delete Request ===
async function deleteRequest(token) {
    if (!(await customConfirm('Are you sure you want to delete this request?'))) return;

    try {
        const response = await fetch(`/api/requests/${token}`, { method: 'DELETE' });
        if (response.ok) {
            await loadRequests();
            customAlert("Request deleted successfully!", "Deleted", "success");
        } else {
            customAlert("Failed to delete request", "Error", "error");
        }
    } catch (error) {
        console.error('Error deleting request:', error);
        customAlert(`Network error: ${error.message}`, "Error", "error");
    }
}

// === Search Request by Token ===
async function searchRequest(event) {
    event.preventDefault();
    const token = document.getElementById('search-token').value.trim();
    const resultDiv = document.getElementById('request-result');

    if (!token) return;

    resultDiv.classList.remove('d-none');
    resultDiv.innerHTML = `
        <div class="text-center text-muted py-2">
            <div class="spinner-border spinner-border-sm" role="status"></div>
            <span class="ms-2">Searching...</span>
        </div>`;

    try {
        const response = await fetch(`/api/requests/${encodeURIComponent(token)}`);
        if (!response.ok) throw new Error('Request not found');

        const req = await response.json();
        const residuesList = (req.residues || []).map(r => `<li>${r.name || `Residue #${r.id}`}</li>`).join('') || '<li><em>No residues</em></li>';
        const statusesList = (req.statuses || []).map(s => `<li>${s.name || s.status || 'Unknown'} (${s.timestamp || ''})</li>`).join('') || '<li><em>No statuses</em></li>';

        resultDiv.innerHTML = `
            <div class="card border-light shadow-sm">
                <div class="card-body">
                    <h6 class="fw-bold mb-3 text-primary">Request Details</h6>
                    <ul class="list-unstyled mb-2">
                        <li><strong>Token:</strong> <span class="text-monospace">${req.token}</span></li>
                        <li><strong>Status:</strong> <span class="badge bg-info text-dark">${req.requestStatus}</span></li>
                        <li><strong>Municipality:</strong> ${req.municipality}</li>
                        <li><strong>Date & Time:</strong> ${new Date(req.datetime).toLocaleString()}</li>
                    </ul>
                    <hr class="my-3">
                    <div><h6 class="fw-semibold text-secondary mb-1">Residues:</h6><ul>${residuesList}</ul></div>
                    <div><h6 class="fw-semibold text-secondary mb-1">Statuses:</h6><ul>${statusesList}</ul></div>
                </div>
            </div>`;
    } catch (error) {
        resultDiv.innerHTML = `
            <div class="alert alert-danger mt-3 mb-0">
                <i class="bi bi-exclamation-triangle"></i> ${error.message}
            </div>`;
        customAlert(error.message, "Error", "error");
    }
}

// === Initialize on Page Load ===
document.addEventListener("DOMContentLoaded", () => {
    loadResidues();
    loadMunicipalities();
    loadRequests();
});
