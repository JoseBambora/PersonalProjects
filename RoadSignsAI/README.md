# Road Signals Classification

The primary goal of this project is to evaluate different AI models for their ability to identify road signals in images.

The road signals considered are categorized into the following types:
- Stop Signs
- Traffic Lights (Green or Red)
- Speed Limit Signs (including the speed limit values)

The dataset used for training is available on [Kaggle](https://www.kaggle.com/datasets/pkdarabi/cardetection).

For this task, I utilized three different pre-trained models:
1. [Residual Net (ResNet)](https://pytorch.org/vision/stable/models/generated/torchvision.models.resnet18.html#torchvision.models.resnet18)
2. [Efficient Net](https://pytorch.org/vision/stable/models/generated/torchvision.models.efficientnet_b0.html#torchvision.models.efficientnet_b0)
3. [Mobile Net](https://pytorch.org/vision/stable/models/generated/torchvision.models.mobilenet_v2.html#torchvision.models.mobilenet_v2)

Regarding the fine-tuning process, I created a [Kaggle Notebook](https://www.kaggle.com/code/josebambora/road-sign-detection-ai-train) using Kaggle P100 GPUs. Training and testing these models took nearly four hours. The notebook includes documentation related to the code.

Feel free to reuse the [notebook](https://www.kaggle.com/code/josebambora/road-sign-detection-ai-train) or the [fine-tuned models](https://www.kaggle.com/code/josebambora/road-sign-classification/output).

# Results

The table below summarizes the results from fine-tuning the models. It is evident that Mobile Net performed the worst, which is expected given its simplicity and smaller size. The comparison between Residual Net and Efficient Net is inconclusive; while ResNet performed better during training, Efficient Net yielded better results during testing.

| Model        | Training Loss | Validation Loss | Validation Accuracy | Test Accuracy |
|:------------:|:-------------:|:---------------:|:-------------------:|:-------------:|
| Residual Net | 0.1009        | 0.3573          | 89.39%              | 86.03%        |
| Efficient Net| 0.3385        | 0.3631          | 88.64%              | 86.81%        |
| Mobile Net   | 0.2736        | 0.4961          | 84.39%              | 82.73%        |

# Future Work

Instead of simple road signs classification, I plan to fine-tune some pre-trained models for computer vision task, such as YOLO. With this approach, I aim to develop AI models that can both identify the location of road signs in images and classify them.

# References

1. Kaiming He, Xiangyu Zhang, Shaoqing Ren, and Jian Sun. "Deep Residual Learning for Image Recognition." arXiv, 2015. [https://arxiv.org/abs/1512.03385](https://arxiv.org/abs/1512.03385).

2. Mingxing Tan and Quoc V. Le. "EfficientNet: Rethinking Model Scaling for Convolutional Neural Networks." arXiv, 2020. [https://arxiv.org/abs/1905.11946](https://arxiv.org/abs/1905.11946).

3. Mark Sandler, Andrew Howard, Menglong Zhu, Andrey Zhmoginov, and Liang-Chieh Chen. "MobileNetV2: Inverted Residuals and Linear Bottlenecks." arXiv, 2019. [https://arxiv.org/abs/1801.04381](https://arxiv.org/abs/1801.04381).