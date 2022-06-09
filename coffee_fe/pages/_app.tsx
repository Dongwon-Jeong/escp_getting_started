import '../styles/globals.css'
import type { AppProps } from 'next/app'
import wrapper from "../reducers/store/configureStore";

function RootApp({ Component, pageProps }: AppProps) {
  return <Component {...pageProps} />
}

export default wrapper.withRedux(RootApp);
