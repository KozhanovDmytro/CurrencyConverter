package com.implemica.CurrencyConverter.model;

/**
 * Enumeration of currencies.
 *
 * @author Dmytro K.
 */
public enum Currency {

   AED, AFN, ALL, AMD, ANC, ANG, AOA, ARN, ARS, AUD, AUR, AVT, AWG, AZN, BAM, BAT,
   BBD, BC, BCC, BCH, BCA, BLK, BDT, BGC, BGN, BHD, BIF, BMD, BND, BOB, BYN, BRL,
   BSD, BTC, BTG, XBT, BTN, BWP, BYR, BZD, CAD, CDF, CHF, CLF, CLP, CNC, CNY, COP,
   CRC, CUP, CVE, CZK, DASH, DCR, DGB, DJF, DKK, DOGE, ERN, XDC, XDG, DOP, DGC, DVC,
   DRK, DZD, EDO, EEK, EGD, EGP, EOS, ETB, ETC, ETH, EUR, FJD, _1ST, FKP, FTC, GBP,
   GEL, GHS, GHs, GIP, GMD, GNF, GNO, GNT, GTQ, GVT, GYD, HKD, HVN, HNL, HRK, HTG,
   HUF, ICN, IDR, ILS, INR, IOC, IOT, IQD, IRR, ISK, IXC, JEP, JMD, JOD, JPY, KES,
   KGS, KHR, KICK, KMF, KPW, KRW, KWD, KYD, KZT, LAK, LBP, LSK, LKR, LRD, LSL, LTC,
   XLT, LTL, LVL, LYD, MAD, MDL, MEC, MGA, MKD, MLN, MMK, MNT, MOP, MRO, MSC, MUR,
   MVR, MWK, MXN, MYR, MZN, NAD, NOBS, NEO, NGN, NIO, NMC, NOK, NPR, NVC, NXT, NZD,
   OMG, OMR, PAB, PEN, PGK, PHP, PKR, PLN, POT, PPC, PYG, QAR, QRK, QTUM, REP, RON,
   RSD, RUB, RUR, RWF, SAR, SBC, SBD, SC, SCR, SDG, SEK, SGD, SHP, SLL, SMART, SOS,
   SRD, START, STEEM, STD, STR, STRAT, SVC, SYP, SZL, THB, TJS, TMT, TND, TOP, TRC,
   TRY, TTD, TWD, TZS, UAH, UGX, USD, USDT, USDE, UTC, UYU, UZS, VEF, VET, VEN, XVN,
   VIB, VND, VUV, WDC, WST, XAF, XAS, XAUR, XCD, XDR, XEM, XLM, XMR, XRB, XOF, XPF,
   XPM, XRP, YBC, YER, ZAR, ZEC, ZEN, ZMK, ZMW, ZRC, ZWL, H18, M18, U18, Z18, H19,
   M19, BNK, BNB, QSP, IOTA, YOYO, BTS, ICX, MCO, CND, XVG, POE, TRX, ADA, FUN, HSR,
   LEND, ELF, STORJ, MOD, GWP, SKK, SIT, MZM, IEP, NLG, ZWN, GHC, MGF, ESP, ZWR, USN,
   TRL, XBD, CYP, LUF, SRG, XPT, ADP, TPE, COU, BEF, AFA, ROL, DEM, BOV, ATS, XUA, CHE,
   PTE, VEB, AYM, ZWD, USS, CSD, XTS, BYB, XFU, XSU, TMM, AZM, XFO, SDD, YUM, MTL, FIM,
   CHW, XBA, XXX, UYI, XBC, GRD, XBB, BGL;

   /**
    * Gets instance of {@link Currency} by code.
    * @param code code
    * @return instance
    * @throws IllegalArgumentException if currency does not exist.
    */
   public static Currency getInstance(String code) {
      return Currency.valueOf(code);
   }

   /**
    * Gets String representation of currency.
    * @return code
    */
   public String getCurrencyCode() {
      return name();
   }
}
