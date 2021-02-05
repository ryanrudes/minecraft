import numpy as np
import matplotlib.pyplot as plt

def render3D(block, opacity=1, size=20):
    X, Y, Z = np.mgrid[:block.shape[0], :block.shape[1], :block.shape[2]]
    fig = plt.figure(figsize = (7, 7))
    ax = fig.add_subplot(111, projection = '3d')
    colors = [(*(np.array(block[x, z, y].color.rgba[:3]) / 255), min(block[x, z, y].color.rgba[3], opacity)) for x in range(block.shape[0]) for z in range(block.shape[1]) for y in range(block.shape[2])]
    ax.scatter(X.flatten(), Y.flatten(), Z.flatten(), c = colors, marker = "s", s = size)
    plt.show()
