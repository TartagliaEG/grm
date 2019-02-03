
export default THEME = () => {
  const theme = DARK();

  return ({
    screen: { flex: 1, flexDirection: 'column', backgroundColor: theme.surfacePrimaryColor },

    screenHeader: { flexDirection: 'row', paddingLeft: theme.gapMD, paddingRight: theme.gapMD, paddingTop: theme.gapXS, paddingBottom: theme.gapXS },
    screenContent: { flex: 1, flexDirection: 'column' },
    screenContentCentralized: { flex: 1, flexDirection: 'column', justifyContent: 'center' },

    listItem: { paddingLeft: theme.gapMD, paddingRight: theme.gapMD, paddingTop: theme.gapXS, paddingBottom: theme.gapXS },

    headingPrimary: { color: theme.headingPrimaryColor, fontSize: theme.headingPrimarySize, fontWeight: theme.headingPrimaryWeight },
    contentPrimary: { color: theme.contentPrimaryColor, fontSize: theme.contentPrimarySize, fontWeight: theme.contentPrimaryWeight },
    contentPrimaryBig: { color: theme.contentPrimaryColor, fontSize: theme.contentPrimarySize * 1.2, fontWeight: theme.contentPrimaryWeight },
    surfacePrimary: { backgroundColor: theme.backgroundColor },
  });
}

export const DARK = () => ({
  surfacePrimaryColor: '#000',

  contentPrimaryColor: '#FFF',
  contentPrimarySize: 14,
  contentPrimaryWeight: 'normal',

  headingPrimaryColor: '#FFF',
  headingPrimarySize: 20,
  headingPrimaryWeight: 'bold',

  gapXL: 64,
  gapLG: 48,
  gapMD: 32,
  gapSM: 16,
  gapXS: 10,
});


export const combine = (...args) => {
  return args.reduce((prev, cur) => ({ ...prev, ...cur }), {});
}
