let map;
let userMarker;
let destinationMarker;

function initialize(){
  initMap();
  initAutocomplete();
}
function initAutocomplete() {
    // Define center and default bounds outside the Autocomplete constructor
    const center = { lat: 40.76909, lng: -73.98415 };
    const distanceKm = 0.67;
    const latDelta = distanceKm / 111.32;
    const lngDelta = distanceKm / (111.32 * Math.cos((Math.PI / 180) * center.lat));

    const defaultBounds = {
        north: center.lat + latDelta,
        south: center.lat - latDelta,
        east: center.lng + lngDelta,
        west: center.lng - lngDelta,
    };

    // Get the input element
    const input = document.getElementById("searchInput");

    // Define Autocomplete options
    const options = {
        bounds: defaultBounds,
        componentRestrictions: { country: "us" },
        fields: ["address_components", "geometry", "icon", "name"],
        strictBounds: false,
    };

    // Initialize Autocomplete with the input element and options
    const autocomplete = new google.maps.places.Autocomplete(input, options);
    const getDirectionBtn = document.getElementById("getDirectionBtn");

    // Attach click event listener to Get Direction button
    getDirectionBtn.addEventListener("click", function() {
        // Get the selected place
        const place = autocomplete.getPlace();
 
        // Check if a place is selected and it has geometry
        if (place.geometry && place.geometry.location) {
            // Retrieve latitude and longitude from the selected place's geometry
            const latitude = place.geometry.location.lat();
            const longitude = place.geometry.location.lng();
            const message=latitude+","+longitude;
            Unity.call(message);
        } else {
            console.error("No geometry found for the selected place.");
        }
    });
}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 40.7696, lng: -73.9824 },
        zoom: 17
    });
}

function search() {
    const searchInput = document.getElementById('searchInput').value;

    // Initialize Places Autocomplete service
    const autocompleteService = new google.maps.places.AutocompleteService();
    autocompleteService.getPlacePredictions({ input: searchInput }, (predictions, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK && predictions && predictions.length > 0) {
            const placeId = predictions[0].place_id;
            const service = new google.maps.places.PlacesService(map);
            service.getDetails({ placeId: placeId }, (place, status) => {
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    const location = place.geometry.location;
                    map.setCenter(location);
                    if (destinationMarker) {
                        destinationMarker.setMap(null);
                    }
                    destinationMarker = new google.maps.Marker({
                        position: location,
                        map: map,
                        title: place.name
                    });

                    // Calculate walking route
                    calculateRoute(location);
                } else {
                    console.error('Place details not found');
                }
            });
        } else {
            console.error('Place predictions not found');
        }
    });
}

function calculateRoute(destination) {
    const directionsService = new google.maps.DirectionsService();
    const directionsRenderer = new google.maps.DirectionsRenderer({ map: map });

    const request = {
        origin: userMarker.getPosition(),
        destination: destination,
        travelMode: 'WALKING'
    };

    directionsService.route(request, (response, status) => {
        if (status === 'OK') {
            directionsRenderer.setDirections(response);
        } else {
            console.error('Error calculating route:', status);
        }
    });
}

// Function to calculate distance between two points
function calculateDistance(point1, point2) {
    return google.maps.geometry.spherical.computeDistanceBetween(point1, point2);
}

// Update distance from destination
function updateDistance() {
    if (destinationMarker && userMarker) {
        const distance = calculateDistance(userMarker.getPosition(), destinationMarker.getPosition());
        document.getElementById('distance').innerText = `Distance to destination: ${distance.toFixed(2)} meters`;
    }
}

// Update distance every 5 seconds
setInterval(updateDistance, 5000);
