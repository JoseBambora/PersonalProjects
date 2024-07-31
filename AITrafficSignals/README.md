# AI for Classifying Traffic Signals

The main goal of this project is to create an AI model that identifies and classifies traffic signals in images.

In this notebook, I have created a CNN model to classify images of traffic signals. The signals can be categorized into the following types:
- Stop Signs
- Traffic Lights (Green or Red)
- Speed Limit Signs (and the velocity associated)

The dataset used is published on [Kaggle](https://www.kaggle.com/datasets/pkdarabi/cardetection). **Disclaimer**: I only used the image data.

For this task, I have used two different models: one being a Convolutional Neural Network that I created, and the other being a Residual Neural Network pre-trained from [PyTorch](https://arxiv.org/abs/1512.03385)[1].

# Models

## CNN

Details about the architecture, training method, and so on.

## ResNet

Same as CNN.

# Results

# To Do

- Complete this documentation.
- Fix some issues in the notebook used in Kaggle.
- Organize the code into different files.

# References

**Deep Residual Learning for Image Recognition**  
*Kaiming He, Xiangyu Zhang, Shaoqing Ren, Jian Sun*  
arXiv, 2015. [https://arxiv.org/abs/1512.03385](https://arxiv.org/abs/1512.03385)