//
//  FruitView.swift
//  AssignmentThree
//
//  Created by Mikael Ngo on 2023-10-12.
//

import SwiftUI

struct FruitView: View {
    var fruit: String

    var body: some View {
        VStack {
            Text(fruit)
        }
        .padding()
    }
}

struct FruitView_Previews: PreviewProvider {
    static var previews: some View {
        FruitView(fruit: "Apple")
    }
}
