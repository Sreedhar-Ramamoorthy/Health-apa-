
<!DOCTYPE html>
<html>

<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;900&display=swap" />
</head>

<body
    style="display: flex; flex-direction:column; justify-content:center; align-items:center; width:100%; box-sizing:border-box">
    <div>
        <table style="background-color: #FFFFFF;" width="100%">
            <tbody>
                <tr>
                    <td>
                        <table align="center" border="0" cellpadding="0" cellspacing="0" class="row row-1"
                            role="presentation" width="100%">

                            <tbody>
                                <tr
                                    style="padding: 0 24px 0 0px;box-sizing: border-box;text-align: left;font-size: 20px;font-family: Inter;color: #004ca8;">
                                    <td style="text-align: start;">
                                        <div style="padding: 0 0 11px;box-sizing: border-box;">
                                            <h3
                                                style="margin: 0;align-self: stretch;font-size: inherit;line-height: 24px;font-weight: 700;font-family: inherit;">
                                                JAMII PLUS - APP QUOTATION</h3>
                                        </div>
                                    </td>
                                    <td style="text-align: center;">
                                        <img src="https://www.apainsurance.org/images/AppResources/8VcqfzV.png"
                                            style="height: 105px;position: relative;object-fit: cover;">
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </td>
                </tr>

                <tr style="text-align: center; margin-top: 300px">
                    <td style="font-size: 14px;color: #073063;font-family: Inter;">

                        <div style="width: 390px;margin:20px auto; padding: 0 0 0 32px;box-sizing: border-box;">
                            <div
                                style="border-radius: 8px;background-color: #e3f2fd;overflow: hidden;padding: 2px;white-space: nowrap;">
                                <div style="line-height: 24px;">
                                    <span style="font-size: 18px;font-weight: 600;">
                                        <span>Principal Member:</span>
                                        <span style="color: #004ca8;"> </span>
                                    </span>
                                    <span style="font-size: 18px;color: #004ca8;">
                                        <span style="font-weight: 900;">${holderName}</span>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>

                <tr style="text-align: start;">
                    <td
                        style="padding: 10px 0px 5px;box-sizing: border-box;text-align: left;font-family: Inter;color: #073063;">

                        <b style="font-size: 16px;line-height: 24px;white-space: nowrap;">Client
                            Details</b>
                    </td>
                </tr>
            </tbody>
        </table>

        <table style="width: 100%;box-sizing: border-box;">
            <tbody>
                <tr>
                    <td style="padding: 2px 0px;text-align: left;font-family: Inter;color: #073063;">
                        <table>
                            <tr>
                                <td style="font-size: 14px;line-height: 24px;white-space: nowrap;">
                                    <b>Quotation Number:</b>
                                </td>
                                <td
                                    style="font-size: 14px;font-weight:bold;font-family: Inter;color: #004CA8;line-height: 24px;white-space: nowrap;">
                                    ${quoteNumber}
                                </td>

                                <td style="font-size: 14px;line-height: 24px;white-space: nowrap;">
                                    <b style="margin-left: 230px">Date of Birth:</b>
                                </td>

                                <td
                                    style="font-size: 14px;font-weight:bold;font-family: Inter;color: #004CA8;line-height: 24px;white-space: nowrap;">
                                    ${dateOfBirth}

                                </td>
                            </tr>
                        </table>
                    </td>


                    <td style="padding: 2px 30px;text-align: left;font-family: Inter;color: #073063;">
                        <table>
                            <tr>

                            </tr>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>

        <hr style="height:0.8px;border-width:0;color:rgb(193, 193, 193);background-color:rgb(211, 211, 211)">


        <table style="width: 100%;box-sizing: border-box;">
            <tbody>
                <tr style="text-align: start;">
                    <td
                        style="padding: 10px 0px 5px;box-sizing: border-box;text-align: left;font-family: Inter;color: #073063;">

                        <b style="font-size: 16px;line-height: 24px;white-space: nowrap;">Dependants Details</b>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;font-family: Inter;color: #004CA8;">
                        <table style="width: 100%;">
                            <tr>
                                <td style="width: 33% !important;">
                                    <b style="font-size: 14px;white-space: nowrap;">Spouse</b>
                                </td>

                                <td style="width: 33% !important;">
                                    <p
                                        style="font-size: 14px;font-family: Inter;color: #000;line-height: 24px;white-space: nowrap; margin:0 18px;">
                                        ${isSpouse}
                                    </p>
                                </td>
                                <td style="width: 33% !important;">
                                    <b style="font-size: 13px;line-height: 24px;white-space: nowrap;">DOB:</b>
                                    <span
                                        style="font-size: 13px;font-family: Inter;color: #000;line-height: 24px;white-space: nowrap;">${spouseDateOfBirth}
                                    </span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;font-family: Inter;color: #004CA8;">
                        <table style="width: 100%;">
                            <tr>
                                <td style="width: 33% !important;">
                                    <b style="font-size: 14px;white-space: nowrap;">Unmarried Children</b>
                                </td>

                                <td style="width: 33% !important;">
                                    <p
                                        style="font-size: 14px;font-family: Inter;color: #000;line-height: 24px;white-space: nowrap; margin:0 18px;">
                                        ${noOfChildren}
                                    </p>
                                </td>
                                <td style="width: 33% !important;"></td>
                            </tr>
                            <tr>
                                <td></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>

        <hr style="height:0.8px;border-width:0;color:rgb(193, 193, 193);background-color:rgb(211, 211, 211)">


        <table style="width: 100%;box-sizing: border-box;">
            <tbody>
                <tr style="text-align: start;">
                    <td
                        style="padding: 10px 0px 5px;box-sizing: border-box;text-align: left;font-family: Inter;color: #073063;">

                        <b style="font-size: 16px;line-height: 24px;white-space: nowrap;">Selected Benefits</b>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;font-family: Inter;color: #004CA8;">
                        <table style="width: 100%;">
                            <tr>
                                <td style="width: 33% !important;">
                                    <ul
                                        style="padding:0 10px; margin:10px 0 20px; padding-left: 20px; list-style-type:disc">
                                        <li style="font-size: 13px;"><b>Inpatient - </b><span>Ksh
                                                ${benefitValues.inpatientLimit}</span> </li>
                                    </ul>
                                </td>

                                <td style="width: 33% !important;">
                                    <ul
                                        style="padding:0 10px; margin:10px 0 20px; padding-left: 20px; list-style-type:disc">
                                        <li style="font-size: 13px;"><b>Maternity - </b><span>Ksh
                                                ${benefitValues.maternityLimit}</span> </li>
                                    </ul>
                                </td>
                            </tr>

                            <tr>
                                <td style="width: 33% !important;">
                                    <ul style="padding:0 10px; margin:0;list-style-type:disc;padding-left: 20px;">
                                        <li style="font-size: 13px;"><b>Outpatient - </b><span>Ksh
                                                ${benefitValues.outpatientLimit}</span> </li>
                                    </ul>
                                </td>

                                <td style="width: 33% !important;">
                                    <ul style="padding:0 10px; margin:0;list-style-type:disc;padding-left: 20px;">
                                        <li style="font-size: 13px;"><b>Travel - </b><span>${travel}</span> </li>
                                    </ul>
                                </td>
                            </tr>

                            <tr>
                                <td style="width: 33% !important;">
                                    <b style="margin-left:15px; color:#004CA8;font-size: 13px;">- Dental</b><span
                                        style="color:#004CA8; font-size: 13px;"> - Ksh
                                        ${benefitValues.dentalLimit}</span>
                                </td>
                            </tr>

                            <tr>
                                <td style="width: 33% !important;">
                                    <b style="margin-left:15px; color:#004CA8;font-size: 13px;">- Optical</b><span
                                        style="color:#004CA8; font-size: 13px;"> -
                                        Ksh ${benefitValues.opticalLimit}</span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>
        <hr style="height:0.8px;border-width:0;color:rgb(193, 193, 193);background-color:rgb(211, 211, 211)">


        <table>
            <tbody>
                <tr style="text-align: start;">

                    <td
                        style="padding: 10px 0px 0px;box-sizing: border-box;text-align: left;font-family: Inter;color: #073063;">

                        <b style="font-size: 16px;line-height: 24px;white-space: nowrap;">Premium Payable:
                            KES ${premium}
                        </b>
                    </td>
                </tr>

                <tr>
                    <td style="width: 33% !important; padding-bottom: 10px">
                        <b style="font-size: 14px;white-space: nowrap; color:#004CA8;font-family:Inter">Status :
                            Quotation Stage
                        </b>
                    </td>
                </tr>
            </tbody>
        </table>

        <hr style="height:0.8px;border-width:0;color:rgb(193, 193, 193);background-color:rgb(211, 211, 211)">


        <table style="width:100%">
            <tbody>
                <tr style="text-align: start;">
                    <td
                        style="padding-top:10px;box-sizing: border-box;text-align: left;font-family: Inter;color: #073063;">
                        <b style="font-size: 16px;line-height: 24px;white-space: nowrap;">Agent Details
                        </b>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;font-family: Inter;color: #004CA8; ">
                        <table style="width: 100%; ">
                            <tr>
                                <td style="width: 33% !important; padding-bottom: 10px">
                                    <b style="font-size: 14px;white-space: nowrap;">Full Name: ${agentName}
                                    </b>
                                </td>

                                <td style="width: 33% !important; padding-bottom: 10px">
                                    <b style="font-size: 14px;white-space: nowrap;">Agent Branch: ${agentBranch}</b>
                                </td>
                            </tr>
                    </td>
                </tr>
            </tbody>
        </table>

        <hr style="height:0.8px;border-width:0;color:rgb(193, 193, 193);background-color:rgb(211, 211, 211)">

        <table style="width:100%">
            <tbody>
                <tr style="text-align: start;">
                    <td style="box-sizing: border-box;text-align: left;font-family: Inter;color: #004CA8;">
                        <p
                            style="font-size: 13px;line-height: 24px;white-space: nowrap;font-family: Inter;color: #004CA8;">
                            APA General Health (Kenya)
                            Limited, Apollo Centre, 07 Ring Road Parklands, Westlands, <br>P.O Box 30065, 00100 Nairobi,
                            Kenya,<br>Tel: +254 (20) 2862 000,<br>Email: info@apainsurance.org
                        </p>

                        <img src="https://www.apainsurance.org/images/AppResources/sOPgJxn.png"
                            style="width: 250px;position: relative;object-fit: cover;">
                    </td>
                </tr>
            </tbody>
        </table>

        <table style="width: 100%; margin-top:25px">
            <tbody>
                <tr>
                    <td style="height: 11px;flex: 1;position: relative;background-color: #0c4da2;min-width: 190px;">
                    </td>
                    <td
                        style="height: 11px;flex: 1;position: relative;background-color: #ed2328;min-width: 196px;z-index: 1;">
                    </td>
                </tr>
            </tbody>
        </table>

    </div>
</body>
</html>