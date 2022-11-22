

type RibnAuthorizeAnswer = {
    enabled: boolean,
    walletAddress: string
};


type RibnPlugin = {
    authorize: () => Promise<RibnAuthorizeAnswer>,
    revoke: () => Promise<void>,
    signTransaction: (any) => Promise<any>
};

declare const topl: RibnPlugin;