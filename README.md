# VirtualWiscardReceiver

**VirtualWiscardReceiver** is an application developed to simulate NFC signal reception for [VirtualWiscard](https://github.com/Eric-Erdman/Virtual-Wiscard) services. The app is designed to check if the VirtualWiscard login account has the necessary permissions for access control.

## Features

1. **NFC Signal Simulation**
   - `PASS` Button: Simulates receiving an NFC signal indicating the account has access permissions.
   - `FAIL` Button: Simulates receiving an NFC signal indicating the account does not have access permissions.

2. **Dynamic Interface**
   - Upon receiving a "PASS" signal, the app's background turns green, displaying "Access Granted."
   - Upon receiving a "FAIL" signal, the app's background turns red, displaying "Access Denied."

3. **VirtualWiscard Integration**
   - This app works as part of the **VirtualWiscard**, primarily used to verify permissions associated with a user's VirtualWiscard login account.
