//
//  ContentView.swift
//  AssignmentThree
//
//  Created by Mikael Ngo on 2023-10-12.
//

import SwiftUI

struct ContentView: View {
    @State var fruits = ["Apelsin", "Banan", "Citron"]

    var body: some View {
        NavigationStack {
            VStack {
                List(fruits, id: \.self) { fruit in
                    NavigationLink(
                        destination: FruitView(fruit: fruit)
                    ) {
                        Text(fruit)
                    }
                }
            }
            .padding()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
