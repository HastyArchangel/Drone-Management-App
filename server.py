from flask import Flask, jsonify, request
import math

app = Flask(__name__)

drones = [
    {
        "id": "1",
        "name": "Drone 1",
        "model": "Model A",
        "location": "Warehouse 1",
        "coordinates": {"lat": 44.4268, "lng": 26.1025},
        "speed": 10.0,
        "destination": {"lat": 45.0, "lng": 26.5} 
    },
    {
        "id": "2",
        "name": "Drone 2",
        "model": "Model B",
        "location": "Warehouse 2",
        "coordinates": {"lat": 44.8000, "lng": 26.5000},
        "speed": 12.5,
        "destination": {"lat": 45.1, "lng": 26.6}
    },
    {
        "id": "3",
        "name": "Drone 3",
        "model": "Model C",
        "location": "Warehouse 3",
        "coordinates": {"lat": 44.5000, "lng": 26.8000},
        "speed": 8.0,
        "destination": {}  
    }
]

def move_drone(drone):  
    if not drone["destination"]:
        return

    current = drone["coordinates"]
    dest = drone["destination"]

    distance = math.sqrt((dest["lat"] - current["lat"])**2 + (dest["lng"] - current["lng"])**2)
    if distance < 0.0001:
        drone["destination"] = None
        return

    move_ratio = drone["speed"] / distance * 0.0001  
    drone["coordinates"]["lat"] += (dest["lat"] - current["lat"]) * move_ratio
    drone["coordinates"]["lng"] += (dest["lng"] - current["lng"]) * move_ratio

@app.route('/drones', methods=['GET'])
def get_drones():
    # try:
    #     print("Received GET request data:", request.get_json()) 
    # except Exception as e:
    #     print(f"Exception {e}")
    for drone in drones:
        move_drone(drone)
    return jsonify(drones)

@app.route('/drones/<drone_id>', methods=['PUT'])
def update_drone(drone_id):
    try:
        data = request.get_json()
        print(f"Received PUT request data for drone {drone_id}: {data}")
    except Exception as e:
        print(f"Exception {e}")
    drone = next((d for d in drones if d['id'] == drone_id), None)
    if not drone:
        return jsonify({"message": "Drone not found"}), 404

    for key, value in data.items():
        if key == 'coordinates':
        #     coordinates = data['coordinates']
        #     drone['coordinates'] = {
        #         'lat': coordinates.get('latitude', drone['coordinates'].get('lat')),
        #         'lng': coordinates.get('longitude', drone['coordinates'].get('lng'))
        #     }
        # elif key == 'destination':
        #     destination = data['destination']
        #     drone['destination'] = {
        #         'lat': destination.get('latitude', drone['destination'].get('lat')),
        #         'lng': destination.get('longitude', drone['destination'].get('lng'))
        #     }
            drone['coordinates'] = value
        elif key == 'destination':
            drone['destination'] = value
        elif key in drone:
            drone[key] = value

    for key, value in drone.items():
        print(f"{key} : {value}")

    return jsonify(drone)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
