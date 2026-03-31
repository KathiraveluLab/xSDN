import networkx as nx
import random
import os
import xml.etree.ElementTree as ET

def generate_network_xml(graph, filename):
    root = ET.Element("nodes")
    for node in graph.nodes():
        node_id = f"GSD{node+1}" # Changed to GSD starting from 1 to avoid potential issues
        node_elem = ET.SubElement(root, "node", id=node_id)
        for neighbor in graph.neighbors(node):
            neighbor_id = f"GSD{neighbor+1}"
            link_elem = ET.SubElement(node_elem, "link", id=neighbor_id)
            cost = ET.SubElement(link_elem, "cost")
            cost.text = str(round(random.uniform(1, 100), 2))
            speed = ET.SubElement(link_elem, "speed")
            speed.text = str(round(random.uniform(1, 100), 2))
            energy = ET.SubElement(link_elem, "energy")
            energy.text = str(round(random.uniform(1, 100), 2))
            
    tree = ET.ElementTree(root)
    tree.write(filename, xml_declaration=True, encoding="UTF-8")

def generate_flows_xml(graph, flows_filename, init_filename):
    base_flows = [
        {"id": "F1", "profile": "energy", "start": "0", "chunks": "1.2,34,234,453,44,33"},
        {"id": "F2", "profile": "bandwidth", "start": "100", "chunks": "100,30.1D,334,24,203D,3,303D,4334,3, 199, 300"},
        {"id": "F3", "start": "200", "chunks": "100,300.1G,334,24,203G,3,303G,4334,3, 199, 300"},
        {"id": "F4", "start": "300", "chunks": "4.4*15"},
        {"id": "F5", "start": "400", "chunks": "40.1+200.1D*100"},
        {"id": "F6", "start": "500", "chunks": "40+200G*100"},
        {"id": "F7", "start": "600", "chunks": "R+200G*100"},
        {"id": "F8", "start": "700", "chunks": "10000.1/20+200G"}
    ]
    
    nodes = list(graph.nodes())
    
    root_flows = ET.Element("flows")
    root_init = ET.Element("flows")

    for f in base_flows:
        origin_node = random.choice(nodes)
        dest_node = random.choice(nodes)
        while dest_node == origin_node:
            dest_node = random.choice(nodes)
            
        orig_str = f"GSD{origin_node+1}"
        dest_str = f"GSD{dest_node+1}"

        # flows.xml entry
        flow_elem = ET.SubElement(root_flows, "flow", id=f["id"])
        if "profile" in f:
            flow_elem.set("profile", f["profile"])
        start_elem = ET.SubElement(flow_elem, "start")
        start_elem.text = f["start"]
        chunks_elem = ET.SubElement(flow_elem, "chunks")
        chunks_elem.text = f["chunks"]
        orig_elem = ET.SubElement(flow_elem, "origin")
        orig_elem.text = orig_str
        dst_elem = ET.SubElement(flow_elem, "destination")
        dst_elem.text = dest_str

        # initFlows.xml entry
        init_elem = ET.SubElement(root_init, "flow", id="I" + f["id"])
        orig_init = ET.SubElement(init_elem, "origin")
        orig_init.text = orig_str
        dst_init = ET.SubElement(init_elem, "destination")
        dst_init.text = dest_str
        
    tree_flows = ET.ElementTree(root_flows)
    tree_flows.write(flows_filename, xml_declaration=True, encoding="UTF-8")

    tree_init = ET.ElementTree(root_init)
    tree_init.write(init_filename, xml_declaration=True, encoding="UTF-8")

def main():
    # Use relative path dynamically based on script location so it works on other machines
    base_dir = os.path.dirname(os.path.abspath(__file__))
    conf_dir = os.path.join(base_dir, "..", "conf", "benchmarking")
    os.makedirs(conf_dir, exist_ok=True)
    
    # 1. Random Graph (Erdos-Renyi)
    random_graph = nx.erdos_renyi_graph(15, 0.3)
    while not nx.is_connected(random_graph):
        random_graph = nx.erdos_renyi_graph(15, 0.3)
    generate_network_xml(random_graph, os.path.join(conf_dir, "network_random.xml"))
    generate_flows_xml(random_graph, os.path.join(conf_dir, "flows_random.xml"), os.path.join(conf_dir, "init_flows_random.xml"))
    
    # 2. Small World Graph (Watts-Strogatz)
    small_world = nx.watts_strogatz_graph(15, 4, 0.1)
    while not nx.is_connected(small_world):
        small_world = nx.watts_strogatz_graph(15, 4, 0.1)
    generate_network_xml(small_world, os.path.join(conf_dir, "network_smallworld.xml"))
    generate_flows_xml(small_world, os.path.join(conf_dir, "flows_smallworld.xml"), os.path.join(conf_dir, "init_flows_smallworld.xml"))
    
    # 3. Caveman Graph (Connected Caveman)
    caveman = nx.connected_caveman_graph(3, 5)
    generate_network_xml(caveman, os.path.join(conf_dir, "network_caveman.xml"))
    generate_flows_xml(caveman, os.path.join(conf_dir, "flows_caveman.xml"), os.path.join(conf_dir, "init_flows_caveman.xml"))
    
    print("Graph generation complete!")

if __name__ == "__main__":
    main()
