/* ========================================
   API Configuration
======================================== */

const API_BASE_URL =
    "http://localhost:8080/api/leads";

/* ========================================
   Global Variables
======================================== */

let allLeads = [];

let currentFilter = "ALL";

/* ========================================
   Window Load
======================================== */

window.onload = function () {
    loadLeads();
};

/* ========================================
   Load Leads
======================================== */

async function loadLeads() {

    try {

        const response =
            await fetch(API_BASE_URL);

        allLeads = await response.json();

        displayLeads(allLeads);

        updateStats();

    } catch (error) {

        console.error(
            "Error loading leads:",
            error
        );

        document.getElementById(
            "leadsTableBody"
        ).innerHTML =
            `
            <tr>
                <td colspan="8"
                    class="no-leads-message">
                    Failed to load leads.
                    Please refresh the page.
                </td>
            </tr>
            `;
    }
}

/* ========================================
   Display Leads
======================================== */

function displayLeads(leads) {

    const tbody =
        document.getElementById(
            "leadsTableBody"
        );

    if (leads.length === 0) {

        tbody.innerHTML =
            `
            <tr>
                <td colspan="8"
                    class="no-leads-message">
                    No leads found.
                </td>
            </tr>
            `;

        return;
    }

    tbody.innerHTML = leads.map(lead => `
        <tr>
            <td>${lead.id}</td>

            <td>${lead.name}</td>

            <td>${lead.email}</td>

            <td>${lead.phoneNumber}</td>

            <td>${lead.businessType}</td>

            <td>
                <span class="status-badge ${lead.status}">
                    ${lead.status}
                </span>
            </td>

            <td>
                ${formatDate(lead.createdAt)}
            </td>

            <td>
                <div class="action-buttons">

                    <button
                        class="btn-action btn-view"
                        onclick="viewLead(${lead.id})"
                    >
                        View
                    </button>

                    <button
                        class="btn-action btn-status"
                        onclick="updateStatus(${lead.id})"
                    >
                        Update
                    </button>

                </div>
            </td>
        </tr>
    `).join("");
}

/* ========================================
   Update Statistics
======================================== */

function updateStats() {

    const newLeads =
        allLeads.filter(
            l => l.status === "NEW"
        ).length;

    const contactedLeads =
        allLeads.filter(
            l => l.status === "CONTACTED"
        ).length;

    const closedLeads =
        allLeads.filter(
            l => l.status === "CLOSED"
        ).length;

    document.getElementById(
        "newLeadsCount"
    ).textContent = newLeads;

    document.getElementById(
        "contactedLeadsCount"
    ).textContent = contactedLeads;

    document.getElementById(
        "closedLeadsCount"
    ).textContent = closedLeads;

    document.getElementById(
        "totalLeadsCount"
    ).textContent = allLeads.length;
}

/* ========================================
   Filter By Status
======================================== */

function filterByStatus(status) {

    currentFilter = status;

    document.querySelectorAll(
        ".filter-btn"
    ).forEach(btn => {
        btn.classList.remove("active");
    });

    event.target.classList.add("active");

    if (status === "ALL") {

        displayLeads(allLeads);

    } else {

        const filtered =
            allLeads.filter(
                lead => lead.status === status
            );

        displayLeads(filtered);
    }
}

/* ========================================
   Search Leads
======================================== */

async function searchLeads() {

    const keyword =
        document.getElementById(
            "searchInput"
        ).value.trim();

    if (!keyword) {

        displayLeads(allLeads);

        return;
    }

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/search?keyword=${encodeURIComponent(keyword)}`
            );

        const results =
            await response.json();

        displayLeads(results);

    } catch (error) {

        console.error(
            "Error searching leads:",
            error
        );

        alert(
            "Search failed. Please try again."
        );
    }
}

/* ========================================
   Search Input Enter Key
======================================== */

document.getElementById(
    "searchInput"
).addEventListener("keypress", function (e) {

    if (e.key === "Enter") {
        searchLeads();
    }
});

/* ========================================
   View Lead
======================================== */

function viewLead(leadId) {

    const lead =
        allLeads.find(
            l => l.id === leadId
        );

    if (!lead) {
        return;
    }

    const modalBody =
        document.getElementById(
            "modalBody"
        );

    modalBody.innerHTML = `
        <div class="modal-field">
            <div class="modal-label">
                Name:
            </div>

            <div class="modal-value">
                ${lead.name}
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Email:
            </div>

            <div class="modal-value">
                ${lead.email}
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Phone:
            </div>

            <div class="modal-value">
                ${lead.phoneNumber}
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Business Type:
            </div>

            <div class="modal-value">
                ${lead.businessType}
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Status:
            </div>

            <div class="modal-value">
                <span class="status-badge ${lead.status}">
                    ${lead.status}
                </span>
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Message:
            </div>

            <div class="modal-value">
                ${lead.message || "No message provided"}
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Created At:
            </div>

            <div class="modal-value">
                ${formatDate(lead.createdAt)}
            </div>
        </div>

        <div class="modal-field">
            <div class="modal-label">
                Last Updated:
            </div>

            <div class="modal-value">
                ${formatDate(lead.updatedAt)}
            </div>
        </div>
    `;

    document.getElementById(
        "leadModal"
    ).style.display = "block";
}

/* ========================================
   Update Status
======================================== */

function updateStatus(leadId) {

    const lead =
        allLeads.find(
            l => l.id === leadId
        );

    if (!lead) {
        return;
    }

    const modalBody =
        document.getElementById(
            "modalBody"
        );

    modalBody.innerHTML = `
        <div class="modal-field">

            <div class="modal-label">
                Lead: ${lead.name}
            </div>

        </div>

        <div class="modal-field">

            <div class="modal-label">
                Current Status:
            </div>

            <div class="modal-value">
                <span class="status-badge ${lead.status}">
                    ${lead.status}
                </span>
            </div>

        </div>

        <div class="modal-field">

            <div class="modal-label">
                Update Status:
            </div>

            <select
                id="newStatus"
                class="status-select"
            >

                <option
                    value="NEW"
                    ${lead.status === "NEW" ? "selected" : ""}
                >
                    New
                </option>

                <option
                    value="CONTACTED"
                    ${lead.status === "CONTACTED" ? "selected" : ""}
                >
                    Contacted
                </option>

                <option
                    value="CLOSED"
                    ${lead.status === "CLOSED" ? "selected" : ""}
                >
                    Closed
                </option>

            </select>

            <button
                class="btn-update"
                onclick="saveStatus(${leadId})"
            >
                Save
            </button>

        </div>
    `;

    document.getElementById(
        "leadModal"
    ).style.display = "block";
}

/* ========================================
   Save Status
======================================== */

async function saveStatus(leadId) {

    const newStatus =
        document.getElementById(
            "newStatus"
        ).value;

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/${leadId}/status`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        status: newStatus
                    })
                }
            );

        const result =
            await response.json();

        if (result.success) {

            closeModal();

            loadLeads();

        } else {

            alert(
                "Error: " + result.message
            );
        }

    } catch (error) {

        console.error(
            "Error updating status:",
            error
        );

        alert(
            "Failed to update status. Please try again."
        );
    }
}

/* ========================================
   Close Modal
======================================== */

function closeModal() {

    document.getElementById(
        "leadModal"
    ).style.display = "none";
}

/* ========================================
   Close Modal On Outside Click
======================================== */

window.onclick = function (event) {

    const modal =
        document.getElementById(
            "leadModal"
        );

    if (event.target === modal) {
        closeModal();
    }
};

/* ========================================
   Format Date
======================================== */

function formatDate(dateString) {

    if (!dateString) {
        return "N/A";
    }

    const date =
        new Date(dateString);

    return date.toLocaleDateString(
        "en-US",
        {
            year: "numeric",
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        }
    );
}