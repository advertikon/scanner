/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author max
 */
public class Decoder {
	static protected String codes[] = { "FjrDgMOCUyM=", "VAJRcMKhwozDizI=", "RGxT", "N8Oow5vCvMOmwqzCgg==", "w5fCscOHM8O7dcK1DjY=", "H8Kbw5nDusOew7rCow==", "YC/DsA==", "w4BCATPDiUHCp8OgwpY=", "w5txUsO3CVRGw7DCl19Ge2zClBxGScO0", "w5hbwrxG", "w73DosOAwoB/Nk8=", "TcOUNDbDhHLDrSJXw47CmsOdZsKdwoM=", "wqIJwrdiw7I=", "wo05ZsOtPsO4", "w7dLbw==", "UsOBaDhXXcKRw4lvSsOIwonCmMKkLGtt", "XWEUCMOY", "UwVTd8KNwp0=", "wqZSw78=", "ZyTDug==", "UXfDtQ==", "GsOwwpk=", "KMKFw50=", "H8Olwr8=", "fMKVOQZ2dQ==", "GcKpw54=", "w7nDr8OVwqZXLw==", "PcOvwp4=", "B8KywqY=", "MMKMwqnClcKwbQ==", "S8KrNg==", "UXxy", "w41LFijDr1Q=", "aTrDug==", "H8K7w5ImwrTCtA==", "I3w8", "F8OCwow=", "w5JdaQ==", "wrFxw4s=", "w5BXwpk=", "CcO0LQ==", "YMKBPw==", "w6QXBQ==", "M8KWw7I=", "BcKdw7Q=", "aDV/", "LMOyZA==", "w5huwqE=", "w7/Dh30=", "N8Ovw5TCvcOKwq/Cg8Kow5wW", "wr7CpcOXwqXCuMK2wpNYw5XCsQ==", "UwVTd8KPwobDgSNOw4E=", "HcOmwqzDr8KSPcK+w4B5wp7Cu8KUb8OWwrARF8K9aMOfwrfDgMKIwobCm15Iw45uD8KYw6HDhRvCohPCusOsw6vDnkbCgml/w5hOQBDDnFBnRXXCp8Kmw71sw7hUw7BFw5Z8Ow==", "WWbDug==", "D8O/Ew==", "ScOUMzbDg3LDrSJUw47Cng==", "RsK4HWFq", "Z8KtFk1mwqE=", "w7twacOoKSfCgMKhw6TDtsKVw78Iwoc=", "bcKeHMO3Hg==", "wpHChMOX", "w7JZJipWKg==", "XWQaEsOYw4Q=", "w4xCfsKyOAbChQ==", "dRrDqcO3Uy8=", "HsK0w7g=", "w5LDvXDCizlgfw==", "XAhcYsK4woE=", "w4MdHsKA", "w4lyw7rCmH3Dtg==", "w41MGDHDh0U=", "dFVPw5Y8", "w6R+w7E=", "eMKwwoTDuUhbw5cbUcO3fA7DsMOdahjCs2nDpkECMkdILsKXw4HDocOsecOB", "dR/Dp8OtUw==", "DMK8w4M=", "w4V/wq8=", "wpIpUA==", "w4dUCg==", "G8OAYjjCs2HCuzsgw4Y=", "w41Ow5A=", "w5F4w4fCi3vDtz9K", "eMKvMA==", "aQVw", "ezxx", "wq/Ci8Kp", "wpoowpg=", "aEDDvQ==", "LTjDqA==", "SD1AwqxpIA==", "F8OIOzcbUw==", "wrjCvhzCrRLCksKQBA==", "GcK7TA==", "w592Sg==", "WTDDlMOxUzDCpsOW", "bADDosOq", "CMOxworCqQA=", "QErDh0hCwq0=", "w5Viw6fClw==", "w5XDg8O9", "w5ICOQ==", "OsO2wqA=", "InLCrMKrK8K4", "wqxqw4w=", "WWgR", "w5kfLQ==", "wqYSwq8=", "wpZSNsKPWAAsT8KcR8KHN8KDccK8FgJ4cCvChgfCmcO8w656w4rDkyt+WjUtwofDim/CtFIyw6c=", "wp/CjMOC", "c8KYNhNDaQ==", "wonDj8K+NTY8TzhFDg==", "DQTDqQ==", "w6pCAz8=", "w416RcOMBFx2", "w7rCq8Or", "C3sNw6fChxLDig==", "WsKsw5w=", "wo/Cq8Om", "b8Knw5DCt8O5wqnClcKow65f", "w67DqMOzwplCCF7CrMKWWQc=", "UwJdbsKlwow=", "w4HCicO0", "w4gkVQ==", "wofCjMK9", "UEha", "Z8KEwp/DisKjE8OEwqc=", "HsOzwpA=", "ZwBL", "w5ppw5o=", "I1fDkQ==", "w6ptw6w=", "w6t1Gw==", "w4TCqgQ=", "ecKgw4E=", "woLDgsKQ", "JcOmwpbCrw1P", "GcK3wr4=", "w6TCthE=", "w4Z4w7rCjH3DrCROD8OWw5s=", "w7DCvxfCvxDChsKBSw==", "D3EXw4DCmg3DmiMmGMOQ", "w77DosOWwqFxPE/CrA==", "TXMw", "RyLDtQ==", "RipD", "Q2PDgg==", "wpUYwq9u", "w7wgL8Kqwrsoew==", "QHjDrg==", "wqbCpio=", "aTjDvsO2wq7Djw==", "cyRC", "csK/Gw==", "wqQXwr9uw6AOwp1PGw==", "EMKRw5nDrsOFw74=", "wrlJw6U=", "wq3DqsK5", "bsKQwrrCksKU", "DTbDgMOBSDw=", "w5Ndcg==", "wozCqsKi", "wpNFFjbDnUU=", "CMOYNyMbSQ==", "HsOnwqo=", "wrkJwpM=", "QcKSw5bDpcOCw7M=", "wpLCu8KGwrvDtw==", "Fl7Dtg==", "RHnDhw==", "wp/DicK7IhM6RThg", "HsKEw5Y=", "CSPDpg==", "fE/DisOKZkfDvyzCtA4=", "w5ZXPA==", "Q3jDmw==", "wrXCvcO+", "wph5w6HCk2U=", "aUTDtcO1dQA=", "woXCpxg=", "XcK4OQ==", "Undg", "cgDDmMOwVTTCvsOS", "OMOiw5vCqMO9wqg=", "KsOOVQ==", "wqnCuRTCpBnCjsKQGW3CqsO1wrV3wr/DjcOn", "w7bDosOawrNiMw==", "woDCn8Kr", "TRBd", "w4xfw6c=", "OcOvwo3CrxBJwr8CGMKzZVHDuMKFNwrDtg==", "w6nCscKEwqvDisKlw4RBwqQ=", "wpLCkjVHTQ==", "dylB", "wo3ChzdJTcKk", "cit3", "UcKnw6A=", "dgPDvsOjTjPCj8OQwr0/Ow==", "w7lXw5/Cqm8bWQ==", "dlvDrA==", "H8Opwoo=", "H8KBwps=", "LMOIwprDjMK+FcKmw61IwqDDjcK2TcK4wo8tM8KIUsOlwpE=", "wp3Dr8Kv", "DMKYw4LDrsOYw7jCmcOSTEPCrMKrGx8FEELCg3nDhsO3EMK5w6XCg8Oh", "CD3DisODRBvCoA==", "w6Vew4TCqnId", "wpEwfcOtI8O+wqZmT2zDu8Oaw6XCisKMwqXCpXQRwo3DrifDpy5NwqJNAMOFwqfCvhFVwpLCg8K/", "CVPCsQ==", "QAFHYsKlwofDlg==", "wobDgsKxIAE7", "wpvCgMK+cCtnbcOswrI=", "w41vXcOxGQ==", "w79Dw50=", "DcOGZy/ClmfCsTsF", "SHgUBMOCw5dlR8OuT8OPwq3ChWcow7R/wqjDqMKtwrM=", "w5PDrMOY", "wq7CvcOawr7Cjw==", "QnQWBsOYw54=", "w51TGzPDmg==", "CMOHcw==", "SwTDpA==", "KFTDgMOLe27DsA==", "w6rDssOHwrw=", "wpckfw==", "w5sed8OQw5YdcA==", "wqFlw4LCvnrCvBPCo3Y=", "QMKJAMOxE8OR", "Tj3DqQ==", "LMOIwprDjMK+FcKmw61IwqDDjcKoTsOtwpgoKMKcYMOiwr/CtsK2wq3DonFHw4hoDcKUw6nDhxc=", "w45zRMO/BF9g", "AsOUNSEBQMOPw5A=", "QH5YBMOUw4JtTMK6T8OTw6M=", "LMOvOw==", "w6hgcQ==", "w4vDv2HCiShBRkI8BMKD", "wr0uwpw=", "QMKWwoUp", "wo/Cgy9HXsKtK8K4wp4=", "CWYQw4DCmgw=", "RMKYwpouPEfCmWvDiDTDjFrDtcKIwoAa", "w50TbsOCw5o=", "wo89fsOjLcOxwo1sRTbCp8ODw7vCoMKIwrDCjw==", "w6pbwqRWwpU=", "w4fCncO7ZXh6KcO/w7HDjsOdwpnCjsKPJsOmw7U5LVrChMKiwrNnccOpwq8=", "WHAUFMOJ", "w4BCATPDiUHCp8OgwpbDozYLa2TDgw/CpD0=", "OHbCrsK5Og==", "An8Pw5rCiR7Dmy8gWcOVwpJTwogweggmPw==", "w60kN8KLwrc=", "fCzDtsOxwrzDkyjCoQ==", "b8KRLRNebyVmwp5vQRcNwrtTw4Ji", "TSPDo8OswqzDmQLCg0DDq8KWbiI=", "w7tEIT5WMQ==", "w6lZKiZLNsO3wq7DlQ==", "YFnDssOhdRs=", "cWEQAMOCw4JnTw==", "w4zChMOkf2to", "w4HCvsOPOsONdMK6BSbDjxQ=", "NsKcwqHClMKFag==", "w4rClMO/Y3J+", "w7HCohzCuQPCkg==", "w44ZA8KQIsKH", "LkrDgcOcYg==", "McO/w5zCvMO9wrM=", "w43Cr8OR", "RcOEw6dzw6nDgg==", "w4jDsnLCjzNG", "wr3CsMOYw4PCiF0=", "MsKTwq3ClMKedMKwOjE=", "LMORwonDjcK+FcK9w61Gwr3Ck8K9", "wrFzw4/CqGjCpg==", "w4FjJiRFKsOWwpHDuGrCqA==", "woTCmjBdTcK/", "PgDDi8OKWTrCr8Ozw7VFJDNMw6MEwozDhgjCiMKwwpZi", "woHCosKDwr3DpsKq", "SEDDilpbwqDClhEVe8OQw77DqcK/UcKrNMOnagJkAsK5csOWw4tFE0PDhA==", "T8KBwoU0L1U=", "w7pTKzhPJ8OMwojCtzzCrsKIw67Dgm3CgsOFfcOvw57DjMOFw6nClmg7wohWPsO4w4vCsiTCgcO/woDCnw==", "OcOcwobDmMKjCA==", "wqjCusOew5PCmV1yw5kwSgrDuwzDpCU=", "w4fCp8OKJcOpbw==", "w7UkLcKXwrUkaiTCncK9wqMBw53CvcOdwrrCtgw=", "w7rCuwPCoxDCgMKQVnrDssO7wqphwq7DjA==", "wo/Dn8K2NAEg", "wo89fsOjLcOxwo1sRTbCpcOcw6XCgMKMwr7Cnntewoo=", "EsKVw4HDoMOWw7fCssOYRhnDvcKpFBYeHlHDhiHCjcKxP8Or", "w5TCvsOPI8O4", "E8OUNyAAVsKMw5otQsKZwofDusKxeGM3G0km", "cA7Dp8OxQg==", "ckjDtcO2bh/Dt2sPcFkPw6vDlMOLM8OUwo7DnivCvg==", "IsOmw5nCusOs", "wq/CocOfw5TCk1kvwqAjTT/DhDfDriXDiMOfw6nDolXDmMOjCMKpw6kdFMOE", "GMK8w5AhwpjCpWNWw7DCuMKGTcOkw5bDpG9bw6zCnTjClg==", "c0DDt8OnZA==", "OcOSwo7Dh8O5D8KWw5tEwqbCmcK2RcKww5ZvKsKKVcOswpbDvg==", "wpzDhsKzMhA=", "w5PCnmhSDcKwb8Krw5krKA==", "JMKuw4Q=", "EMOhw5k=", "w4vCiMOibQ==", "w5UMAsKV", "w7cqPA==", "w6FKdg==", "MMKWwq3ChsKFfMKcIzknwofChMK2", "w4x6w7M=", "D8KGw5Q=", "wpfCiMKv", "ZsOcwrHCphpGwpAUFcKrYWDDnsKJKwvDsGfCsFgPHSl/W8K/wrTDjsKOOMOEZ8KKKg==", "VjlAwq9yJQ==", "wojCjcKt", "wpTCr8KZwqY=", "ScKBwoAoPA==", "CsKVw5vDvMOU", "w58jNw==", "wqU6ZA==", "bzLDssOkwq7DmR/CoEfDrMKWYyI=", "J3rCpQ==", "w4QKDg==", "Vwpr", "wpgnJMKaJMKeNsK5w5rCosOuEkEpBEZAQwPDqSbDtWhMURTCviw9HcO4FAbCpw==", "w74OKQ==", "AXsKw4DCjxjDig==", "w6VUw40=" };
	protected String[] initialCodes = {"c8KYNhNDaQ==", "wonDj8K+NTY8TzhFDg==", "DQTDqQ==", "w6pCAz8=", "w416RcOMBFx2", "w7rCq8Or", "C3sNw6fChxLDig==", "WsKsw5w=", "wo/Cq8Om", "b8Knw5DCt8O5wqnClcKow65f", "w67DqMOzwplCCF7CrMKWWQc=", "UwJdbsKlwow=", "w4HCicO0", "w4gkVQ==", "wofCjMK9", "UEha", "Z8KEwp/DisKjE8OEwqc=", "HsOzwpA=", "ZwBL", "w5ppw5o=", "I1fDkQ==", "w6ptw6w=", "w6t1Gw==", "w4TCqgQ=", "ecKgw4E=", "woLDgsKQ", "JcOmwpbCrw1P", "GcK3wr4=", "w6TCthE=", "w4Z4w7rCjH3DrCROD8OWw5s=", "w7DCvxfCvxDChsKBSw==", "D3EXw4DCmg3DmiMmGMOQ",  		"w77DosOWwqFxPE/CrA==", "TXMw", "RyLDtQ==", "RipD", "Q2PDgg==", "wpUYwq9u", "w7wgL8Kqwrsoew==", "QHjDrg==", "wqbCpio=", "aTjDvsO2wq7Djw==", "cyRC", "csK/Gw==", "wqQXwr9uw6AOwp1PGw==", "EMKRw5nDrsOFw74=", "wrlJw6U=", "wq3DqsK5", "bsKQwrrCksKU", "DTbDgMOBSDw=", "w5Ndcg==", "wozCqsKi", "wpNFFjbDnUU=", "CMOYNyMbSQ==", "HsOnwqo=", "wrkJwpM=", "QcKSw5bDpcOCw7M=", "wpLCu8KGwrvDtw==", "Fl7Dtg==", "RHnDhw==", "wp/DicK7IhM6RThg", "HsKEw5Y=", "CSPDpg==", "fE/DisOKZkfDvyzCtA4=", "w5ZXPA==", "Q3jDmw==", "wrXCvcO+",  		"wph5w6HCk2U=", "aUTDtcO1dQA=", "woXCpxg=", "XcK4OQ==", "Undg", "cgDDmMOwVTTCvsOS", "OMOiw5vCqMO9wqg=", "KsOOVQ==", "wqnCuRTCpBnCjsKQGW3CqsO1wrV3wr/DjcOn", "w7bDosOawrNiMw==", "woDCn8Kr", "TRBd", "w4xfw6c=", "OcOvwo3CrxBJwr8CGMKzZVHDuMKFNwrDtg==", "w6nCscKEwqvDisKlw4RBwqQ=", "wpLCkjVHTQ==", "dylB", "wo3ChzdJTcKk", "cit3", "UcKnw6A=", "dgPDvsOjTjPCj8OQwr0/Ow==", "w7lXw5/Cqm8bWQ==", "dlvDrA==", "H8Opwoo=", "H8KBwps=", "LMOIwprDjMK+FcKmw61IwqDDjcK2TcK4wo8tM8KIUsOlwpE=", "wp3Dr8Kv", "DMKYw4LDrsOYw7jCmcOSTEPCrMKrGx8FEELCg3nDhsO3EMK5w6XCg8Oh",  		"CD3DisODRBvCoA==", "w6Vew4TCqnId", "wpEwfcOtI8O+wqZmT2zDu8Oaw6XCisKMwqXCpXQRwo3DrifDpy5NwqJNAMOFwqfCvhFVwpLCg8K/", "CVPCsQ==", "QAFHYsKlwofDlg==", "wobDgsKxIAE7", "wpvCgMK+cCtnbcOswrI=", "w41vXcOxGQ==", "w79Dw50=", "DcOGZy/ClmfCsTsF", "SHgUBMOCw5dlR8OuT8OPwq3ChWcow7R/wqjDqMKtwrM=", "w5PDrMOY", "wq7CvcOawr7Cjw==", "QnQWBsOYw54=", "w51TGzPDmg==", "CMOHcw==", "SwTDpA==", "KFTDgMOLe27DsA==", "w6rDssOHwrw=", "wpckfw==", "w5sed8OQw5YdcA==", "wqFlw4LCvnrCvBPCo3Y=", "QMKJAMOxE8OR", "Tj3DqQ==", "LMOIwprDjMK+FcKmw61IwqDDjcKoTsOtwpgoKMKcYMOiwr/CtsK2wq3DonFHw4hoDcKUw6nDhxc=",  		"w45zRMO/BF9g", "AsOUNSEBQMOPw5A=", "QH5YBMOUw4JtTMK6T8OTw6M=", "LMOvOw==", "w6hgcQ==", "w4vDv2HCiShBRkI8BMKD", "wr0uwpw=", "QMKWwoUp", "wo/Cgy9HXsKtK8K4wp4=", "CWYQw4DCmgw=", "RMKYwpouPEfCmWvDiDTDjFrDtcKIwoAa", "w50TbsOCw5o=", "wo89fsOjLcOxwo1sRTbCp8ODw7vCoMKIwrDCjw==", "w6pbwqRWwpU=", "w4fCncO7ZXh6KcO/w7HDjsOdwpnCjsKPJsOmw7U5LVrChMKiwrNnccOpwq8=", "WHAUFMOJ", "w4BCATPDiUHCp8OgwpbDozYLa2TDgw/CpD0=", "OHbCrsK5Og==", "An8Pw5rCiR7Dmy8gWcOVwpJTwogweggmPw==", "w60kN8KLwrc=", "fCzDtsOxwrzDkyjCoQ==", "b8KRLRNebyVmwp5vQRcNwrtTw4Ji",  		"TSPDo8OswqzDmQLCg0DDq8KWbiI=", "w7tEIT5WMQ==", "w6lZKiZLNsO3wq7DlQ==", "YFnDssOhdRs=", "cWEQAMOCw4JnTw==", "w4zChMOkf2to", "w4HCvsOPOsONdMK6BSbDjxQ=", "NsKcwqHClMKFag==", "w4rClMO/Y3J+", "w7HCohzCuQPCkg==", "w44ZA8KQIsKH", "LkrDgcOcYg==", "McO/w5zCvMO9wrM=", "w43Cr8OR", "RcOEw6dzw6nDgg==", "w4jDsnLCjzNG", "wr3CsMOYw4PCiF0=", "MsKTwq3ClMKedMKwOjE=", "LMORwonDjcK+FcK9w61Gwr3Ck8K9", "wrFzw4/CqGjCpg==", "w4FjJiRFKsOWwpHDuGrCqA==", "woTCmjBdTcK/", "PgDDi8OKWTrCr8Ozw7VFJDNMw6MEwozDhgjCiMKwwpZi", "woHCosKDwr3DpsKq",  		"SEDDilpbwqDClhEVe8OQw77DqcK/UcKrNMOnagJkAsK5csOWw4tFE0PDhA==", "T8KBwoU0L1U=", "w7pTKzhPJ8OMwojCtzzCrsKIw67Dgm3CgsOFfcOvw57DjMOFw6nClmg7wohWPsO4w4vCsiTCgcO/woDCnw==", "OcOcwobDmMKjCA==", "wqjCusOew5PCmV1yw5kwSgrDuwzDpCU=", "w4fCp8OKJcOpbw==", "w7UkLcKXwrUkaiTCncK9wqMBw53CvcOdwrrCtgw=", "w7rCuwPCoxDCgMKQVnrDssO7wqphwq7DjA==", "wo/Dn8K2NAEg", "wo89fsOjLcOxwo1sRTbCpcOcw6XCgMKMwr7Cnntewoo=", "EsKVw4HDoMOWw7fCssOYRhnDvcKpFBYeHlHDhiHCjcKxP8Or", "w5TCvsOPI8O4", "E8OUNyAAVsKMw5otQsKZwofDusKxeGM3G0km", "cA7Dp8OxQg==",  		"ckjDtcO2bh/Dt2sPcFkPw6vDlMOLM8OUwo7DnivCvg==", "IsOmw5nCusOs", "wq/CocOfw5TCk1kvwqAjTT/DhDfDriXDiMOfw6nDolXDmMOjCMKpw6kdFMOE", "GMK8w5AhwpjCpWNWw7DCuMKGTcOkw5bDpG9bw6zCnTjClg==", "c0DDt8OnZA==", "OcOSwo7Dh8O5D8KWw5tEwqbCmcK2RcKww5ZvKsKKVcOswpbDvg==", "wpzDhsKzMhA=", "w5PCnmhSDcKwb8Krw5krKA==", "JMKuw4Q=", "EMOhw5k=", "w4vCiMOibQ==", "w5UMAsKV", "w7cqPA==", "w6FKdg==", "MMKWwq3ChsKFfMKcIzknwofChMK2", "w4x6w7M=", "D8KGw5Q=", "wpfCiMKv", "ZsOcwrHCphpGwpAUFcKrYWDDnsKJKwvDsGfCsFgPHSl/W8K/wrTDjsKOOMOEZ8KKKg==",  		"VjlAwq9yJQ==", "wojCjcKt", "wpTCr8KZwqY=", "ScKBwoAoPA==", "CsKVw5vDvMOU", "w58jNw==", "wqU6ZA==", "bzLDssOkwq7DmR/CoEfDrMKWYyI=", "J3rCpQ==", "w4QKDg==", "Vwpr", "wpgnJMKaJMKeNsK5w5rCosOuEkEpBEZAQwPDqSbDtWhMURTCviw9HcO4FAbCpw==", "w74OKQ==", "AXsKw4DCjxjDig==", "w6VUw40=", "FjrDgMOCUyM=", "VAJRcMKhwozDizI=", "RGxT", "N8Oow5vCvMOmwqzCgg==", "w5fCscOHM8O7dcK1DjY=", "H8Kbw5nDusOew7rCow==", "YC/DsA==", "w4BCATPDiUHCp8OgwpY=", "w5txUsO3CVRGw7DCl19Ge2zClBxGScO0", "w5hbwrxG", "w73DosOAwoB/Nk8=", "TcOUNDbDhHLDrSJXw47CmsOdZsKdwoM=",  		"wqIJwrdiw7I=", "wo05ZsOtPsO4", "w7dLbw==", "UsOBaDhXXcKRw4lvSsOIwonCmMKkLGtt", "XWEUCMOY", "UwVTd8KNwp0=", "wqZSw78=", "ZyTDug==", "UXfDtQ==", "GsOwwpk=", "KMKFw50=", "H8Olwr8=", "fMKVOQZ2dQ==", "GcKpw54=", "w7nDr8OVwqZXLw==", "PcOvwp4=", "B8KywqY=", "MMKMwqnClcKwbQ==", "S8KrNg==", "UXxy", "w41LFijDr1Q=", "aTrDug==", "H8K7w5ImwrTCtA==", "I3w8", "F8OCwow=", "w5JdaQ==", "wrFxw4s=", "w5BXwpk=", "CcO0LQ==", "YMKBPw==", "w6QXBQ==", "M8KWw7I=", "BcKdw7Q=", "aDV/", "LMOyZA==", "w5huwqE=", "w7/Dh30=", "N8Ovw5TCvcOKwq/Cg8Kow5wW",  		"wr7CpcOXwqXCuMK2wpNYw5XCsQ==", "UwVTd8KPwobDgSNOw4E=", "HcOmwqzDr8KSPcK+w4B5wp7Cu8KUb8OWwrARF8K9aMOfwrfDgMKIwobCm15Iw45uD8KYw6HDhRvCohPCusOsw6vDnkbCgml/w5hOQBDDnFBnRXXCp8Kmw71sw7hUw7BFw5Z8Ow==", "WWbDug==", "D8O/Ew==", "ScOUMzbDg3LDrSJUw47Cng==", "RsK4HWFq", "Z8KtFk1mwqE=", "w7twacOoKSfCgMKhw6TDtsKVw78Iwoc=", "bcKeHMO3Hg==", "wpHChMOX", "w7JZJipWKg==", "XWQaEsOYw4Q=", "w4xCfsKyOAbChQ==", "dRrDqcO3Uy8=", "HsK0w7g=", "w5LDvXDCizlgfw==", "XAhcYsK4woE=", "w4MdHsKA", "w4lyw7rCmH3Dtg==", "w41MGDHDh0U=",  		"dFVPw5Y8", "w6R+w7E=", "eMKwwoTDuUhbw5cbUcO3fA7DsMOdahjCs2nDpkECMkdILsKXw4HDocOsecOB", "dR/Dp8OtUw==", "DMK8w4M=", "w4V/wq8=", "wpIpUA==", "w4dUCg==", "G8OAYjjCs2HCuzsgw4Y=", "w41Ow5A=", "w5F4w4fCi3vDtz9K", "eMKvMA==", "aQVw", "ezxx", "wq/Ci8Kp", "wpoowpg=", "aEDDvQ==", "LTjDqA==", "SD1AwqxpIA==", "F8OIOzcbUw==", "wrjCvhzCrRLCksKQBA==", "GcK7TA==", "w592Sg==", "WTDDlMOxUzDCpsOW", "bADDosOq", "CMOxworCqQA=", "QErDh0hCwq0=", "w5Viw6fClw==", "w5XDg8O9", "w5ICOQ==", "OsO2wqA=", "InLCrMKrK8K4", "wqxqw4w=",  		"WWgR", "w5kfLQ==", "wqYSwq8=", "wpZSNsKPWAAsT8KcR8KHN8KDccK8FgJ4cCvChgfCmcO8w656w4rDkyt+WjUtwofDim/CtFIyw6c=", "wp/CjMOC"};
	private static final Logger logger = Logger.getLogger( Decoder.class.getName() );
	/**
	 * Decodes script files
	 * @param s Input string
	 * @return
	 */
	static public String decodeScript( String s ) {
		ByteBuffer out = ByteBuffer.allocate( s.length() );
		
		for ( int i = 0, l = s.length(); i < l; i+= 2 ) {
			out.putShort( (short)Integer.parseInt( s.substring( i, i + 2 ), 16 ) );
		}

		String code = new String( out.array(), Charset.forName( "UTF-16" ) );
		code = deHexCode( code );
		
//		System.out.println( code );

		return code;
	}
	
	/**
	 * Replaces all the \xXX sequences to Decimal codes
	 * @param s Input string
	 * @return
	 */
	protected static String deHexCode( String s ) {
		StringBuilder temp = new StringBuilder();
		int start = 0, index = 0;
		
		for( int i = 0, l = s.length(); i < l; i++ ) {
			index = s.indexOf( "\\x", index );

			if ( -1 != index ) {
				ByteBuffer b = ByteBuffer.allocate( 2 );
				b.putShort( (short)Integer.parseInt( s.substring( index + 2, index + 4 ), 16 ) );

				temp.append( s.substring( start, index) ).append( new String( b.array(), Charset.forName( "UTF-16" ) ) );
				index += 4;
				i += 3;
				start = index;

			} else {
				temp.append( s.substring( start, s.length() ) );
				break;
			}
		}
		
		return temp.toString();
	}
	
	/**
	 * Decodes all the crypted strings
	 * @param s Input string
	 * @return
	 */
	static public String decodeNames( String s ) {
		StringBuilder out = new StringBuilder();
		int start = 0, index = 0, argStart = 0, matchStart = 0;
		String arg1 = null, arg2 = null;

		while( true ) {
			index = matchStart = s.indexOf( "_0xf6e3(", index );
			
			//System.out.println( String.format( "Match: %s", s.substring( index, index + 15 ) ) );
			
			if ( -1 < index ) {
				boolean inString = false;
				
				System.out.println( index );

				for ( int l = s.length(); index < l; index++ ) {
					int c = s.codePointAt( index );

					if ( c == '"' ) {
						if ( inString ) {
							if ( null == arg1 ) {
								arg1 = s.substring( argStart, index );

							} else {
								arg2 = s.substring( argStart, index );
							}

						} else {
							argStart = index + 1;
						}
						inString = !inString;
					}
					
					if ( !inString && c == ')' ) {
						break;
					}
				}

				if ( null == arg1 || null == arg2 ) {
					continue;
				}

				out.append( s.substring( start, matchStart ) ).append( decode( arg1, arg2 ) );
				start = ++index;
				arg1 = arg2 = null;

			} else {
				out.append( s.substring( start, s.length() ) );
				break;
			}
		}
		
		return out.toString();
	}
	
	/**
	 * Returns decypted string from codes list under number n usong key fn
	 * @param n
	 * @param fn
	 * @return
	 */
	static public String decode( String n, String fn ) {
		int num = Integer.decode( n );
		String data = codes[ num ];

		return decrypt( data, fn );
	}
	
	/**
	 * Decrypts string data with key fn
	 * @param data
	 * @param fn
	 * @return
	 */
	static public String decrypt( String data, String fn ) {
		int temp;
		StringBuilder tempData = new StringBuilder();

		byte[] decoded = Base64.getDecoder().decode( data );
		
//		Log.dumpAsChar( decoded );

		for (int val = 0, key = decoded.length; val < key; val++) {
//			System.out.print( Integer.toHexString( decoded[ val ] & 0xff ) + " ");
			String hex = "00" + Integer.toHexString( decoded[ val ] & 0xff );
			tempData.append( "%" ).append( hex.substring( hex.length() - 2 ) );
		}

		data = tempData.toString();
//		System.out.println( data );
		
		data = URLDecoder.decode( data );
//		System.out.println( data );

		HashMap<Integer, Integer> secretKey = new HashMap<>();

		for ( int x = 0; x < 256; x++) {
			secretKey.put( x, x );
		}

		for( int x = 0, y = 0; x < 256; x++ ) {
			y = ( y + secretKey.get( x ) + fn.codePointAt( x % fn.length() ) ) % 256;
			temp = secretKey.get( x );
			secretKey.replace( x, secretKey.get( y ) );
			secretKey.replace( y, temp );
		}

		ByteBuffer testResult = ByteBuffer.allocate( data.length() * 2 );

		for (int i = 0, y = 0, x = 0, l = data.length(); i < l; i++) {
			x = ( x + 1 ) % 256;
			y = ( y + secretKey.get( x ) ) % 256;

			temp = secretKey.get( x );
			secretKey.replace( x, secretKey.get( y ) );
			secretKey.replace( y, temp );

			int currentCode = data.codePointAt( i );
			int sx = secretKey.get( x );
			int sy = secretKey.get( y );
			int mask = secretKey.get( ( sx + sy ) % 256 );
			char c = (char)( ( currentCode ^ mask ) & 0x000000ff );
//			System.out.println( c );
			testResult.putChar( c );
		}

		String out = "";

		try {
			out = new String( testResult.array(), "UTF-16" );

		} catch (UnsupportedEncodingException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

//		System.out.println( out );

		return out;
	}
	
	/**
	 * Initializes list of codes by shift/pushing first 214 items of initial codes list
	 */
	protected void initCodes() {		
		String[] t = new String [ initialCodes.length ];
		
		int count = 0;
		int s = 213;

		for ( s = 213; count <= s; count++ ){
		  t[ codes.length - count - 1 ] = initialCodes[ count ];
		}
		
		while ( count < initialCodes.length ) {
			t[ count - s - 1 ] = initialCodes[ count ];
			count++;
		}

		codes = t;
	}
}

