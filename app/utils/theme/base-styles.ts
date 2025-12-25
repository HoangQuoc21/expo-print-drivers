import { colors } from "./colors";

const baseCard = {
  backgroundColor: colors.white,
  padding: 15,
  borderRadius: 8,
  marginBottom: 15,
};

const baseButton = {
  paddingVertical: 10,
  paddingHorizontal: 20,
  borderRadius: 6,
  alignItems: "center" as const,
  justifyContent: "center" as const,
};

const baseButtonText = {
  color: colors.white,
  fontSize: 14,
  fontWeight: "600" as const,
};

const baseStatusCard = {
  ...baseCard,
  borderWidth: 2,
  alignItems: "center" as const,
  justifyContent: "center" as const,
};

const baseStatusText = {
  fontSize: 14,
  fontWeight: "600" as const,
  textAlign: "center" as const,
};

export { baseCard, baseButton, baseButtonText, baseStatusCard, baseStatusText };
