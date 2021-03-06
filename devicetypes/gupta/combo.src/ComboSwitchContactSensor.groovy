/**
 *  Tasmota Combo Switch (Primary) - Contact Sensor (Secondary) [Lite] a Device HandlerDevice Handler
 *
 *  Authors
 *	 - sandeep gupta
 *
 *  Copyright 2019
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
 
metadata {

    definition (name: "Combo Switch Contact Sensor", namespace: "gupta", author: "Sandeep Gupta") {
        capability "Switch"
		capability "Momentary"
        capability "Contact Sensor"	
		
		command "open"
		command "close"
	}

	simulator {
		status "open": "contact:open"
		status "closed": "contact:close"   
		status "on": "switch:on"
		status "off": "switch:off"
		status "toggle": "momentary:push"
    }

    tiles {	
		multiAttributeTile(name:"main", type: "device.switch", width: 6, height: 4){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}		
				
			tileAttribute("device.contact", key: "SECONDARY_CONTROL") {
				attributeState("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "open")				
                attributeState("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "close")
			}
		}
		
		standardTile("contact", "device.contact", width: 2, height: 2) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "open")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "close")
		}
		
		
		standardTile("toggle", "device.switch",  width: 2, height: 2) {
			state("on", label: 'Toggle', action: "push", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("off", label: 'Toggle', action: "push", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
		}

		valueTile("empty", "empty", width: 2, height: 2) {
		}

		main "main"
		details(["main", "contact","empty", "toggle"])
    }

	preferences {
		section("Main") {
			input(name: "linked", type: "bool", title: "Link Switch and Contact Sensor", description: "", required: false)
		}
	}
}


def parse(String description) {
	log.debug "Parsing message  is ${description}"
	def pair = description.split(":")
	switch(pair[0].trim()){
		case 'switch':	
				(pair[1].trim() == "on") ? on() : off();
				break;
		case 'contact':
				(pair[1].trim() == "open") ? open() : close();
				break;
		case 'momentary':
				if (pair[1].trim() == "push") push();
				break;
		default:
				break;
	}
}


def on(){
	if (device.currentValue("switch") == "on") return;
	_on();
}

def off(){
	if (device.currentValue("switch") == "off") return;
	_off();
}

def open(){
	if (device.currentValue("contact") == "open") return;
	_open();
}

def close(){
	if (device.currentValue("contact") == "closed") return;
	_close();
}

def push() {
	(device.currentValue("switch") == "on") ? off() : on()
	log.debug "Sent 'TOGGLE' command for device: ${device}"
}

def _on() {
    sendEvent(name: "switch", value: "on")
	if (linked) sendEvent(name: "contact", value: "open")
	log.debug "Sent 'on' command for device: ${device}"
}

def _off() {
    sendEvent(name: "switch", value: "off")
	if (linked) sendEvent(name: "contact", value: "closed")
	log.debug "Sent 'off' command for device: ${device}"
}

def _open() {
    sendEvent(name: "contact", value: "open")
	if (linked) sendEvent(name: "switch", value: "on")
	log.debug "Sent 'open' command for device: ${device}"
}

def _close() {
    sendEvent(name: "contact", value: "closed")
	if (linked) sendEvent(name: "switch", value: "off")
	log.debug "Sent 'close' command for device: ${device}"
}

