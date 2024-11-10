from flask import Flask, jsonify, request



app = Flask(__name__)



# Example data about drones, including model, location, coordinates, and destination

drones = [

    {

        "id": "1",

        "name": "Drone 1",

        "model": "Model A",

        "location": "Warehouse 1",

        "coordinates": {"lat": 44.4268, "lng": 26.1025},

        "speed": 10.0,

        "destination": {"lat": 45.0, "lng": 26.5}  # Example destination

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

        "destination": None  # No destination

    }

]



@app.route('/drones', methods=['GET'])

def get_drones():

    return jsonify(drones)



@app.route('/drones/<drone_id>', methods=['PUT'])

def update_drone(drone_id):

    # Find the drone with the provided ID

    drone = next((d for d in drones if d['id'] == drone_id), None)

    

    if drone is None:

        return jsonify({"message": "Drone not found"}), 404

    

    # Get the data from the request body

    data = request.get_json()



    # Update drone properties

    if 'name' in data:

        drone['name'] = data['name']

    if 'model' in data:

        drone['model'] = data['model']

    if 'location' in data:

        drone['location'] = data['location']

    if 'coordinates' in data:

        drone['coordinates'] = data['coordinates']

    if 'speed' in data:

        drone['speed'] = data['speed']

    if 'destination' in data:

        drone['destination'] = data['destination']



    return jsonify(drone)



if __name__ == '__main__':

    app.run(host='0.0.0.0', port=5000)

