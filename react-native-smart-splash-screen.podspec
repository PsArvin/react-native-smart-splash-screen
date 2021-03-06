require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-smart-splash-screen"
  s.version      = package['version']
  s.summary      = "React Native Smart Splash Screen component for Android and iOS"

  s.authors      = { "PsArvin" => "PsArvin@126.com" }
  s.homepage     = "https://github.com/PsArvin/react-native-smart-splash-screen"
  s.license      = package['license']
  s.platform     = :ios, "8.0"

  s.source       = { :git => "https://github.com/PsArvin/react-native-smart-splash-screen.git", :tag => "master" }
  s.source_files  = "ios/RCTSplashScreen/RCTSplashScreen/RCTSplashScreen.{h,m}"

  s.dependency 'React'
end
