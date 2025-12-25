import { createNativeStackNavigator } from "@react-navigation/native-stack";

import { HomeScreen, HomeScreenName } from "../screens";

const Stack = createNativeStackNavigator();

export const AppNavigator = () => {
  return (
    <Stack.Navigator
      initialRouteName={HomeScreenName}
      screenOptions={{ headerShown: false }}
    >
      <Stack.Screen name={HomeScreenName} component={HomeScreen} />
    </Stack.Navigator>
  );
};
