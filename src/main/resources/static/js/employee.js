let allRequests = [];

async function loadEmployeeRequests() {
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

document.addEventListener('DOMContentLoaded', loadEmployeeRequests);
