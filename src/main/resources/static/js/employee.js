let allRequests = [];

async function loadRequests() {
    const tbody = document.getElementById('employee-requests-body');
    const filterSelect = document.getElementById('filter-municipality');

    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Loading...</td></tr>`;

    try {
        const res = await fetch('/api/requests');
        if (!res.ok) throw new Error('Failed to fetch requests');
        allRequests = await res.json();

        // Populate filter dropdown (unique municipalities)
        const municipalities = [...new Set(allRequests.map(r => r.municipality))].sort();
        filterSelect.innerHTML = `<option value="">All Municipalities</option>`;
        municipalities.forEach(m => {
            const opt = document.createElement('option');
            opt.value = m;
            opt.textContent = m;
            filterSelect.appendChild(opt);
        });

        renderRequests(allRequests);
    } catch {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Error loading requests</td></tr>`;
    }
}

function renderRequests(list) {
    const tbody = document.getElementById('employee-requests-body');
    tbody.innerHTML = list.length
        ? list.map(r => `
        <tr>
          <td><code>${r.token}</code></td>
          <td>${r.municipality}</td>
          <td><span class="badge bg-info text-dark">${r.requestStatus}</span></td>
          <td class="text-end">${actionButtons(r)}</td>
        </tr>
      `).join('')
        : `<tr><td colspan="4" class="text-center text-muted">No requests found.</td></tr>`;
}

function filterRequests() {
    const selected = document.getElementById('filter-municipality').value;
    const filtered = selected ? allRequests.filter(r => r.municipality === selected) : allRequests;
    renderRequests(filtered);
}

// Load all statuses for a specific request token
async function loadRequestStatuses(event) {
    event.preventDefault();

    const token = document.getElementById("status-token").value.trim();
    const tbody = document.getElementById("status-table-body");

    if (!token) {
        alert("Please enter a request token.");
        return;
    }

    tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">Loading...</td></tr>`;

    try {
        const response = await fetch(`/api/statuses/request/${token}`);
        if (!response.ok) {
            throw new Error(`Request ${token} not found`);
        }

        const statuses = await response.json();
        if (!statuses || statuses.length === 0) {
            tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No statuses found</td></tr>`;
            return;
        }

        // Sort chronologically by datetime
        statuses.sort((a, b) => new Date(a.datetime) - new Date(b.datetime));

        // Render table rows
        tbody.innerHTML = statuses.map(s => `
            <tr>
                <td>${s.id}</td>
                <td>${s.requestStatus}</td>
                <td>${new Date(s.datetime).toLocaleString()}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error(error);
        tbody.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Error loading statuses</td></tr>`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadRequests();
});
