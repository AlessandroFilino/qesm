import networkx as nx
import matplotlib.pyplot as plt
import json
import os

ABS_PATH = os.path.abspath(__file__)
OUTPUT_PATH = f"{os.path.abspath(os.path.join(os.path.dirname(ABS_PATH), os.pardir))}/output"
MEDIA_PATH = f"{os.path.abspath(os.path.join(os.path.dirname(ABS_PATH), os.pardir))}/media"

def addNodes(graph : nx.DiGraph, subgraph_data : dict):
    graph.add_node(subgraph_data["nameType"], label = subgraph_data["quantityInStock"])
    for sub_node in subgraph_data["subGraph"]:
        addNodes(graph, sub_node)
        graph.add_edge(sub_node["nameType"], subgraph_data["nameType"], label = sub_node["quantityNeeded"])

def trace_graph(graph_data):
    G = nx.DiGraph()
    addNodes(G, graph_data)


    # Disegnare il grafo 
    pos = nx.nx_agraph.graphviz_layout(G, prog="dot", args="-Grankdir=BT")

    edge_labels = nx.get_edge_attributes(G, 'label')
    node_labels = nx.get_node_attributes(G, 'label')

    nx.draw(G, pos, with_labels=True, node_color='skyblue', node_size=5000, font_size=12, font_weight='bold')
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=16, label_pos=0.5, font_color='red', font_weight='bold', verticalalignment='center', horizontalalignment='center')
    
    for node, (x, y) in pos.items():
        plt.text(x, y - 10, f"quantity in stock: {node_labels[node]}", horizontalalignment='center', fontweight='bold', fontsize=14)
    # Visualizzare il grafo
    plt.show()


def main():
    file_path = f'{OUTPUT_PATH}/test.json'
    
    # Apertura del file JSON utilizzando la funzione
    with open(file_path, 'r') as file:
        graph_data = json.load(file)
        
    trace_graph(graph_data)

if __name__ == "__main__":
    main()