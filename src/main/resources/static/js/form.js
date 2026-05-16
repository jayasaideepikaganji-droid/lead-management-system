/* ========================================
   API Configuration
======================================== */

const API_BASE_URL =
    "http://localhost:8080/api/leads";

/* ========================================
   Form Submit Event
======================================== */

document.getElementById("leadForm")
    .addEventListener(
        "submit",
        async function (e) {

            e.preventDefault();

            if (!validateForm()) {
                return;
            }

            const submitBtn =
                document.getElementById(
                    "submitBtn"
                );

            const btnText =
                submitBtn.querySelector(
                    ".btn-text"
                );

            const btnLoader =
                submitBtn.querySelector(
                    ".btn-loader"
                );

            /* Disable Button */
            submitBtn.disabled = true;

            btnText.textContent =
                "Submitting...";

            btnLoader.style.display =
                "inline-block";

            /* Form Data */
            const formData = {

                name:
                    document.getElementById(
                        "name"
                    ).value.trim(),

                email:
                    document.getElementById(
                        "email"
                    ).value.trim(),

                phoneNumber:
                    document.getElementById(
                        "phoneNumber"
                    ).value.trim(),

                businessType:
                    document.getElementById(
                        "businessType"
                    ).value,

                message:
                    document.getElementById(
                        "message"
                    ).value.trim()
            };

            try {

                const response =
                    await fetch(
                        API_BASE_URL,
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body: JSON.stringify(
                                formData
                            )
                        }
                    );

                const result =
                    await response.json();

                if (result.success) {

                    document.getElementById(
                        "leadForm"
                    ).style.display = "none";

                    document.getElementById(
                        "successMessage"
                    ).style.display = "block";

                } else {

                    alert(
                        "Error: "
                        + result.message
                    );
                }

            } catch (error) {

                console.error(
                    "Error submitting form:",
                    error
                );

                alert(
                    "Failed to submit form. " +
                    "Please try again."
                );

            } finally {

                submitBtn.disabled = false;

                btnText.textContent =
                    "Submit Inquiry";

                btnLoader.style.display =
                    "none";
            }
        }
    );

/* ========================================
   Validate Form
======================================== */

function validateForm() {

    let isValid = true;

    clearErrors();

    /* Validate Name */
    const name =
        document.getElementById(
            "name"
        ).value.trim();

    if (name.length < 2) {

        showError(
            "name",
            "Name must be at least 2 characters"
        );

        isValid = false;
    }

    /* Validate Email */
    const email =
        document.getElementById(
            "email"
        ).value.trim();

    const emailRegex =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(email)) {

        showError(
            "email",
            "Please enter a valid email address"
        );

        isValid = false;
    }

    /* Validate Phone */
    const phone =
        document.getElementById(
            "phoneNumber"
        ).value.trim();

    const phoneRegex =
        /^[0-9]{10,15}$/;

    if (!phoneRegex.test(phone)) {

        showError(
            "phoneNumber",
            "Phone must be 10-15 digits"
        );

        isValid = false;
    }

    /* Validate Business Type */
    const businessType =
        document.getElementById(
            "businessType"
        ).value;

    if (!businessType) {

        showError(
            "businessType",
            "Please select a business type"
        );

        isValid = false;
    }

    /* Validate Message */
    const message =
        document.getElementById(
            "message"
        ).value.trim();

    if (message.length < 10) {

        showError(
            "message",
            "Message must be at least 10 characters"
        );

        isValid = false;
    }

    return isValid;
}

/* ========================================
   Show Error
======================================== */

function showError(fieldName, message) {

    const field =
        document.getElementById(
            fieldName
        );

    const errorElement =
        document.getElementById(
            fieldName + "Error"
        );

    field.classList.add("error");

    errorElement.textContent = message;
}

/* ========================================
   Clear Errors
======================================== */

function clearErrors() {

    const errorFields =
        document.querySelectorAll(
            ".error"
        );

    errorFields.forEach(field =>
        field.classList.remove("error")
    );

    const errorMessages =
        document.querySelectorAll(
            ".error-message"
        );

    errorMessages.forEach(msg =>
        msg.textContent = ""
    );
}

/* ========================================
   Real-Time Validation
======================================== */

document.querySelectorAll(
    "input, select, textarea"
).forEach(element => {

    element.addEventListener(
        "input",
        function () {

            this.classList.remove("error");

            const errorElement =
                document.getElementById(
                    this.id + "Error"
                );

            if (errorElement) {
                errorElement.textContent = "";
            }
        }
    );
});