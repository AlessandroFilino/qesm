import matplotlib.pyplot as plt
import json
import os
import graphviz



ABS_PATH = os.path.abspath(__file__)
OUTPUT_PATH = f"{os.path.abspath(os.path.join(os.path.dirname(ABS_PATH), os.pardir))}/output"
MEDIA_PATH = f"{os.path.abspath(os.path.join(os.path.dirname(ABS_PATH), os.pardir))}/media"


def trace_graph(dot_graph):
    
    dot_graph = dot_graph.replace('}', 'rankdir=BT; \n}')

    # Crea l'oggetto Graph da Graphviz
    graph = graphviz.Source(dot_graph, format="pdf", engine="dot")


    # Visualizza il grafo
    graph.view()
    # Render the graph to PDF
    graph.render(f"{MEDIA_PATH}/graph.pdf", format="pdf", cleanup=True)


def main():
    
    # Leggi il file .dot
    with open(f'{OUTPUT_PATH}/jGraphT.dot', 'r') as f:
        dot_graph = f.read()

        
    trace_graph(dot_graph)

if __name__ == "__main__":
    main()