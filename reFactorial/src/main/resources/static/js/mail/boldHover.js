
document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item");

    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            // Remove the active class from all menu items
            menuItems.forEach(el => el.classList.remove("active"));

            // Add the active class to the clicked item
            this.classList.add("active");
        });
    });
});

