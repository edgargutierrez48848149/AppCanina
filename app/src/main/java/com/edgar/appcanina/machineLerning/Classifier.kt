package com.edgar.appcanina.machineLerning

import android.graphics.Bitmap
import com.edgar.appcanina.MAX_RECOGNITION_DOG_RESULTS
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.DequantizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.MappedByteBuffer
import java.util.*

class Classifier(tfLiteModel: MappedByteBuffer, private val labels: List<String>) {

    private val imageSizeX: Int

    private val imageSizeY: Int

    private var tfLite: Interpreter

    private var inputImageBuffer: TensorImage

    private val outputProbabilityBuffer: TensorBuffer

    private val tensorProcessor: TensorProcessor

    init {
        val tfLiteOptions = Interpreter.Options()
        tfLiteOptions.setNumThreads(5)
        tfLite = Interpreter(tfLiteModel, tfLiteOptions)

        val imageTensorIndex = 0
        val imageShape = tfLite.getInputTensor(imageTensorIndex).shape()
        imageSizeY = imageShape[1]
        imageSizeX = imageShape[2]
        val imageDataType = tfLite.getInputTensor(imageTensorIndex).dataType()
        val probabilityTensorIndex = 0
        val probabilityShape =
            tfLite.getOutputTensor(probabilityTensorIndex).shape()
        val probabilityDataType = tfLite.getOutputTensor(probabilityTensorIndex).dataType()

        inputImageBuffer = TensorImage(imageDataType)

        outputProbabilityBuffer = TensorBuffer.createFixedSize(
            probabilityShape,
            probabilityDataType
        )

        tensorProcessor = TensorProcessor.Builder().add(DequantizeOp(0f, 1 / 255.0f)).build()
    }

    fun recognizeImage(bitmap: Bitmap): List<DogRecognition> {
        inputImageBuffer = loadImage(bitmap)
        val rewoundOutputBuffer = outputProbabilityBuffer.buffer.rewind()
        tfLite.run(inputImageBuffer.buffer, rewoundOutputBuffer)

        val labeledProbability = TensorLabel(labels, tensorProcessor.process(outputProbabilityBuffer)).mapWithFloatValue

        return getTopKProbability(labeledProbability)
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {

        inputImageBuffer.load(bitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    private fun getTopKProbability(labelProb: Map<String, Float>): List<DogRecognition> {

        val priorityQueue =
            PriorityQueue(MAX_RECOGNITION_DOG_RESULTS) { lhs: DogRecognition, rhs: DogRecognition ->
                (rhs.confidence).compareTo(lhs.confidence)
            }

        for ((key, value) in labelProb) {
            priorityQueue.add(DogRecognition(key, value * 100.0f))
        }

        val recognitions = mutableListOf<DogRecognition>()
        val recognitionsSize = minOf(priorityQueue.size, MAX_RECOGNITION_DOG_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(priorityQueue.poll()!!)
        }

        return recognitions
    }
}