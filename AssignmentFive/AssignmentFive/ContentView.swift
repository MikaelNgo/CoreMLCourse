//
//  ContentView.swift
//  AssignmentFive
//
//  Created by Mikael Ngo on 2023-10-28.
//

import SwiftUI
import Vision

struct ContentView: View {
    @State var resultText = ""
    @State var imageVisible = false
    @State var image: UIImage? = nil

    var body: some View {
        VStack() {
            Text("Första knapptryckningen blir alltid en katt oavsett vilken knapp man trycker först. Efterföljande knapptryckningar blir allting en elefant. Modellen är tränad på det i kursen delade träningssetet med 105 iterationer och med alla augmentations ibockade.")
                .padding(.all)

            HStack() {
                Button("Cat 1") {
                    showAndPredictImage(name: "cat")
                }
                .padding()

                Spacer()

                Button("Cat 2") {
                    showAndPredictImage(name: "somecat")
                }
                .padding()

                Spacer()

                Button("Elephant 1") {
                    showAndPredictImage(name: "elephant")
                }
                .padding()

                Spacer()

                Button("Elephant 2") {
                    showAndPredictImage(name: "elephant1")
                }
                .padding()
            }

            Text(resultText)
                .font(.largeTitle)
                .padding()

            Spacer()

            if imageVisible {
                Image(uiImage: image!)
                    .imageScale(.large)
                    .foregroundColor(.accentColor)
            }
        }
        .padding()
    }

    private func showAndPredictImage(name: String) {
        guard let image = UIImage(named: name) else {
            fatalError("Could not create UIImage")
        }
        self.image = image
        let prediction = image.predict()
        resultText = prediction.asString
        imageVisible = true
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

extension UIImage {
    var pixelBuffer: CVPixelBuffer? {
        let image = self

        let attrs = [kCVPixelBufferCGImageCompatibilityKey: kCFBooleanTrue, kCVPixelBufferCGBitmapContextCompatibilityKey: kCFBooleanTrue] as CFDictionary
        var pixelBuffer : CVPixelBuffer?
        let status = CVPixelBufferCreate(kCFAllocatorDefault, Int(image.size.width), Int(image.size.height), kCVPixelFormatType_32ARGB, attrs, &pixelBuffer)
        guard status == kCVReturnSuccess else {
            return nil
        }

        CVPixelBufferLockBaseAddress(pixelBuffer!, CVPixelBufferLockFlags(rawValue: 0))
        let pixelData = CVPixelBufferGetBaseAddress(pixelBuffer!)

        let rgbColorSpace = CGColorSpaceCreateDeviceRGB()
        let context = CGContext(data: pixelData, width: Int(image.size.width), height: Int(image.size.height), bitsPerComponent: 8, bytesPerRow: CVPixelBufferGetBytesPerRow(pixelBuffer!), space: rgbColorSpace, bitmapInfo: CGImageAlphaInfo.noneSkipFirst.rawValue)

        context?.translateBy(x: 0, y: image.size.height)
        context?.scaleBy(x: 1.0, y: -1.0)

        UIGraphicsPushContext(context!)
        image.draw(in: CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height))
        UIGraphicsPopContext()
        CVPixelBufferUnlockBaseAddress(pixelBuffer!, CVPixelBufferLockFlags(rawValue: 0))

        return pixelBuffer
    }

    func predict() -> Prediction {
        let defaultConfig = MLModelConfiguration()

        let imageClassifierWrapper = try?
            MyImageClassifier(configuration: defaultConfig)

        guard let imageClassifier = imageClassifierWrapper else {
            fatalError("Failed to create an image classifier")
        }

        guard
            let pixelBuffer = pixelBuffer,
            let prediction = try? imageClassifier.prediction(image: pixelBuffer)
        else {
            fatalError("Failed to predict the image")
        }

        let probability = prediction.classLabelProbs.max(by: { prev, current in
            prev.value < current.value
        })!.value

        return Prediction(
            label: prediction.classLabel,
            probability: probability
        )
    }
}

struct Prediction {
    var label: String
    var probability: Double
}

extension Prediction {
    var asString: String {
        return
                """
                Label: \(label)

                Probability: \(probability)
                """
    }
}
