
import AudioToolbox
import AVFoundation
import UIKit

/// Handles playing alarm sounds
class AlarmPlayer {
    var audioPlayer: AVAudioPlayer?
    
    func playAlarm() {
        guard let audioData = NSDataAsset(name: "AlarmSound")?.data else {
            fatalError("AlarmSound asset doesn't exist")
        }
        
        do {
            audioPlayer = try AVAudioPlayer(data: audioData)
            audioPlayer?.prepareToPlay()
            audioPlayer?.play()
        } catch {
            print("Error initializing audio player: \(error.localizedDescription)")
            
        }
    }
    
    func stopAlarm() {
        audioPlayer?.stop()
        audioPlayer = nil
    }
}
