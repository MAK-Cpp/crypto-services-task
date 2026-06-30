from datetime import datetime, timedelta, timezone
from pathlib import Path

from cryptography import x509
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives.serialization import pkcs12
from cryptography.x509.oid import NameOID


ROOT = Path.cwd()
ENV_FILE = ROOT / ".env"


def load_env(path: Path) -> dict[str, str]:
    result = {}

    if not path.exists():
        raise FileNotFoundError(f".env file not found: {path}")

    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()

        if not line or line.startswith("#"):
            continue

        if "=" not in line:
            continue

        key, value = line.split("=", 1)
        result[key.strip()] = value.strip().strip('"').strip("'")

    return result


env = load_env(ENV_FILE)

alias = env["KEYSTORE_ALIAS"]
password = env["KEYSTORE_PASSWORD"].encode("utf-8")

out_file = env.get("KEYSTORE_PATH", "./keystore.p12")
out_path = ROOT / Path(out_file)

if env.get("KEYSTORE_TYPE", "PKCS12") != "PKCS12":
    raise ValueError("Only PKCS12 keystore type is supported")

out_path.parent.mkdir(parents=True, exist_ok=True)

private_key = rsa.generate_private_key(
    public_exponent=65537,
    key_size=2048,
)

subject = issuer = x509.Name([
    x509.NameAttribute(NameOID.COUNTRY_NAME, "RU"),
    x509.NameAttribute(NameOID.ORGANIZATION_NAME, "mak-cpp.ru"),
    x509.NameAttribute(NameOID.ORGANIZATIONAL_UNIT_NAME, "CryptoService"),
    x509.NameAttribute(NameOID.COMMON_NAME, "Document Signing Certificate"),
])

certificate = (
    x509.CertificateBuilder()
    .subject_name(subject)
    .issuer_name(issuer)
    .public_key(private_key.public_key())
    .serial_number(x509.random_serial_number())
    .not_valid_before(datetime.now(timezone.utc))
    .not_valid_after(datetime.now(timezone.utc) + timedelta(days=3650))
    .add_extension(
        x509.BasicConstraints(ca=False, path_length=None),
        critical=True,
    )
    .add_extension(
        x509.KeyUsage(
            digital_signature=True,
            content_commitment=True,
            key_encipherment=False,
            data_encipherment=False,
            key_agreement=False,
            key_cert_sign=False,
            crl_sign=False,
            encipher_only=False,
            decipher_only=False,
        ),
        critical=True,
    )
    .sign(private_key, hashes.SHA256())
)

p12 = pkcs12.serialize_key_and_certificates(
    name=alias.encode("utf-8"),
    key=private_key,
    cert=certificate,
    cas=None,
    encryption_algorithm=serialization.BestAvailableEncryption(password),
)

out_path.write_bytes(p12)

print(f"Generated: {out_path}")
print(f"Alias: {alias}")