
//Changes settings with the device.
//Change to variables instead? Yeah probs
int changeSettings(String command) {

  if (command[0] == "0") {
    ON = command[1].toInt();
  }
  if (command[0] == "1") {
    BRIGHTNESS = 10 + 10 * command[1].toInt();
  }

}
