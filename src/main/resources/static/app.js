let SearchEventInput = document.getElementById("SearchEventInput");

SearchEventInput.addEventListener("input", function(event) {
    let query = event.target.value;
    if (query.length === 0) {
        resetMatchingEventsList();
    } else if (query.length > 0) {
        fetch("http://localhost:8080/Events/Search", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ searchQuery: query })
        })
            .then(response => response.json())
            .then(data => {
                displayEvents(data.matchingEvents);
            })
            .catch(error => console.error("Error:", error));
    }
});

function displayEvents(events) {
    let eventList = document.getElementById("eventList");
    eventList.innerHTML = "";
    if (Array.isArray(events) && events.length > 0) {
        events.forEach(event => {
            let listItem = document.createElement("li");
            listItem.textContent = event.eventName;
            eventList.appendChild(listItem);
        });
    } else {
        let noResults = document.createElement("li");
        noResults.textContent = "No events found.";
        eventList.appendChild(noResults);
    }
}

function resetMatchingEventsList() {
    let eventList = document.getElementById("eventList");
    eventList.innerHTML = "";
}

let SearchShowInput = document.getElementById("SearchShowInput");

SearchShowInput.addEventListener("input", function(show) {
    let query = show.target.value;
    if (query.length === 0) {
        resetMatchingShowsList();
    } else if (query.length > 0) {
        fetch("http://localhost:8080/Shows/Search", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ searchQuery: query })
        })
            .then(response => response.json())
            .then(data => {
                displayShows(data.matchingShows);
            })
            .catch(error => console.error("Error:", error));
    }
});

function displayShows(shows) {
    let showList = document.getElementById("showList");
    showList.innerHTML = "";
    if (Array.isArray(shows) && shows.length > 0) {
        shows.forEach(show => {
            let listItem = document.createElement("li");
            listItem.textContent = show.showName;
            showList.appendChild(listItem);
        });
    } else {
        let noResults = document.createElement("li");
        noResults.textContent = "No shows found.";
        showList.appendChild(noResults);
    }
}

function resetMatchingShowsList() {
    let showList = document.getElementById("showList");
    showList.innerHTML = "";
}
