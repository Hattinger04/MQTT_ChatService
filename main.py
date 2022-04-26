import paho.mqtt.client as paho
from paho import mqtt

import keys

host = keys.host
port = keys.port
username = keys.username
password = keys.password


def on_connect(client, userdata, flags, rc, properties=None):
    print("CONNACK received with code %s." % rc)


def on_publish(client, userdata, mid, properties=None):
    print("mid: " + str(mid))


def on_subscribe(client, userdata, mid, granted_qos, properties=None):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))


def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))


print("Starting...")
client = paho.Client(client_id="", userdata=None, protocol=paho.MQTTv5)
client.on_connect = on_connect


client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)
client.username_pw_set(username, password)
print("Connecting...")
client.connect(host, port)

client.on_subscribe = on_subscribe
client.on_message = on_message
client.on_publish = on_publish

client.subscribe("encyclopedia/#", qos=1)

client.publish("encyclopedia/temperature", payload="hot", qos=1)

client.loop_forever()